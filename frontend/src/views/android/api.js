import { request } from '@/utils'

export default {
  devices: () => request.get('/devices'),
  connect: (params) => request.post('/connect',
    params,
    {
      headers: {
        'Content-Type': 'application/x-www-form-urlencoded',
      },
    }),
}
