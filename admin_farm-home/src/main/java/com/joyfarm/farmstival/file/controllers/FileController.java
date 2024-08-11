package com.joyfarm.farmstival.file.controllers;

import com.joyfarm.farmstival.file.entities.FileInfo;
import com.joyfarm.farmstival.file.services.FileUploadService;
import com.joyfarm.farmstival.global.exceptions.RestExceptionProcessor;
import com.joyfarm.farmstival.global.rests.JSONData;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@RestController
@RequestMapping("/file")
@RequiredArgsConstructor
public class FileController implements RestExceptionProcessor {

    private final FileUploadService uploadService;

    @PostMapping("/upload")
    public ResponseEntity<JSONData> upload(@RequestPart("file") MultipartFile[] files,
                                           @RequestParam(name="gid", required = false) String gid,
                                           @RequestParam(name="location", required = false) String location) {


        List<FileInfo> items = uploadService.upload(files, gid, location);
        HttpStatus status = HttpStatus.CREATED;
        JSONData data = new JSONData(items);
        data.setStatus(status);

        return ResponseEntity.status(status).body(data);
    }


}
