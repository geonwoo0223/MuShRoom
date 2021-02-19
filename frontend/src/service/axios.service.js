import axios from 'axios'
import Config from '../store/config'

export default axios.create({
  baseURL: Config.ServerURL,
  headers: {
    "Content-type": "application/json"
  }
})