import http from "./axios.service"

class sendfile{
    send(frm) {
        return http.post("/upload-file", frm, {
            headers: {
               "Content-Type": "multipart/form-data;"
              },
        })
    }
}

export default new sendfile
