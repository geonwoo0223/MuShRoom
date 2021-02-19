export default {
    props: {
      fileName  : { type: String, default: 'record'   },
      format    : { type: String, default: 'wav'      },
      headers   : { type: Object, default: () => ({}) },
      uploadUrl : { type: String                      }
    }
  }
  