package com.example.twotasksb2.structure.controllers;

import com.example.twotasksb2.structure.services.TaskService;
import com.example.twotasksb2.utils.AdapterUtils;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.nio.charset.StandardCharsets;

/**
 * Controller for file interaction
 */
@RestController
@AllArgsConstructor
@RequestMapping("/file")
public class FileController {
    private final TaskService taskService;

    /**
     *  /file/upload/{taskCode}
     *
     *  Upload file with input
     *  @return answer
     */
    @PostMapping("/upload/{taskCode}")
    public ResponseEntity<String> uploadData(@PathVariable Long taskCode, @RequestParam("file") MultipartFile file) throws Exception {
        if (file == null) throw new RuntimeException("You must select a file for uploading");
        String json = new String (file.getInputStream().readAllBytes(), StandardCharsets.UTF_8);
        String ans = "Input:\n" + AdapterUtils.beautyVersion(json, taskCode) + "\n\nResult:\n" + taskService.calculate(taskCode, json);
        return new ResponseEntity<>(ans, HttpStatus.OK);
    }

    /**
     *  /file/download
     *
     *  Download file with input
     *  @return file as bytes
     */
    @PostMapping("/download")
    public ResponseEntity<byte[]> download(@RequestBody String pojo) {
        byte[] data = pojo.getBytes(StandardCharsets.UTF_8);
        HttpHeaders header = new HttpHeaders();
        header.setContentType(MediaType.TEXT_PLAIN);
        header.setContentLength(data.length);
        header.set("Content-Disposition", "attachment; filename=" + "Input.txt");
        return new ResponseEntity<>(data, header, HttpStatus.OK);
    }
}
