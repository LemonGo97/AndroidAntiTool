import { request } from '@/utils'

export default {
  devices: () => request.get('/devices'),
}
