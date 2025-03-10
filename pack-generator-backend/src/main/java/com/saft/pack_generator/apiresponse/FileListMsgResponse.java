package com.saft.pack_generator.apiresponse;

import org.springframework.http.ResponseEntity;

import java.util.Set;

public class FileListMsgResponse {


    private ResponseEntity<Set<String>> message;


    public FileListMsgResponse(ResponseEntity<Set<String>> message) {
        this.message = message;
    }

    public ResponseEntity<Set<String>> getMessage() {
        return message;
    }

    public void setMessage(ResponseEntity<Set<String>> message) {
        this.message = message;
    }

    @Override
    public String toString() {
        return "FileListMsgResponse{" +
                "message=" + message +
                '}';
    }
}
