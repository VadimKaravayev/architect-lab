package dev.lab.api;

import dev.lab.api.link.Base62;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class Base62Test {

    @Test
    void encode() {
        assertEquals("0", Base62.encode(0));
        assertEquals("1C", Base62.encode(100));
        assertEquals("ZZ", Base62.encode(3843));
        assertEquals("1000000", Base62.encode(56800235584L));
        assertEquals("ZZZZZZZ", Base62.encode(3521614606207L));
        assertThrowsExactly(IllegalArgumentException.class, () -> Base62.encode(-1));
    }
}
