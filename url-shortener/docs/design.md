# URL Shortener Design

## Features

Core:
- Create: user submits a long URL and gets a short URL back
- Redirect: opening a short URL redirects to the long URL

Optional:
- Custom alias chosen by the user
- Link expiry (deleted after a set time)
- Click stats per link
- User account (login to manage own links)

## Targets

- New links per month: 60M
- Redirects per created link: 100
- Redirect speed: 99% of redirects under 50 ms
- Uptime: 99.9%
- Links kept for: 3 years

## Estimates

1 month ≈ 2.5M seconds.

- Creates per second: 60M / 2.5M s = 24 avg; peak ×10 = 240 (daily spikes, viral links)
- Redirects per second: 60M × 100 = 6B/month; 6B / 2.5M s = 2,400 avg; peak ×10 = 24,000 (one viral link)
- Read:write ratio: 100:1, so the redirect path is the system
- Storage per link: code 7 + long URL 100 + created 8 + expiry 8 + user id 8 = 131 B, round up to 200 B (row overhead, indexes, long URLs)
- Total links: 60M × 36 months = 2.2B
- Storage: 200 B × 2.2B = 440 GB; plan ~1 TB with headroom. Fits one disk, so size is not the problem
- Cache: 20% of all links = 88 GB (too big for one node).
  Popular links are mostly recent → 20% of last month: 0.2 × 60M × 200 B = 2.4 GB, fits one node
- Short code length: 7 chars. Need 2.2B; 62^6 = 57B (26× headroom, tight if traffic grows 10×),
  62^7 = 3.5T (1,600× headroom, near-zero collisions for random codes). Extra char costs nothing.

## Bottleneck

- I expect the database to fail first: peak redirects are 24,000/s, mostly for one viral link,
  and one Postgres handles roughly 5–10k reads/s (to be measured in Module 1).
  Plan: put the 2.4 GB cache in front of it.
