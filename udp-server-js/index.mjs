import dgram from 'node:dgram';

const socket = dgram.createSocket('udp4');

socket.on('message', (message, remote) => {
  console.log(`Received "${message}" from ${remote.address}:${remote.port}`);
});

socket.on('listening', () => {
  console.log('Listening on', socket.address());
});

socket.on('error', (error) => {
  console.error('UDP socket error:', error);
  socket.close();
});

socket.bind(5500, '127.0.0.1');
