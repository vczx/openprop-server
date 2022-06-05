package com.vi.openprop.controller;

import com.vi.openprop.dto.LoadResponseDto;
import com.vi.openprop.service.OpenPropService;
import com.vi.openprop.service.URAApiService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.util.Set;

import static org.springframework.http.HttpStatus.OK;

@RestController
@RequestMapping("data")
public class PropDataController {
    private Logger logger = LoggerFactory.getLogger(PropDataController.class);

    private URAApiService uraApiService;
    private OpenPropService openPropService;

    @Autowired
    public PropDataController(URAApiService uraApiService, OpenPropService openPropService) {
        this.uraApiService = uraApiService;
        this.openPropService = openPropService;
    }

    @GetMapping(value = "/init", produces = "application/json")
    public ResponseEntity<String> init() {
        uraApiService.loadURAData();
        return ResponseEntity.status(OK).body("Completed");
    }

    @GetMapping(value = "/loadBatch/{id}", produces = "application/json")
    public ResponseEntity<LoadResponseDto> loadBatch(@PathVariable String id) {
        LoadResponseDto loadResponseDto = new LoadResponseDto();
        try {
            int batch = Integer.parseInt(id);
            if (!Set.of(1, 2, 3, 4).contains(batch)) {
                loadResponseDto.setSuccess(false);
                loadResponseDto.setMessage("Batch should be any of [1,2,3,4]");
                return ResponseEntity.badRequest().body(loadResponseDto);
            }
            loadResponseDto.setSuccess(true);
            uraApiService.loadBatchByNumber(batch);
            return ResponseEntity.status(OK).body(loadResponseDto);
        } catch (Exception e) {
            String error = String.format("Exception for loadbatch by id call with param %s", id);
            logger.error(error,e);
            loadResponseDto.setSuccess(false);
            loadResponseDto.setMessage(error);
            return ResponseEntity.badRequest().body(loadResponseDto);
        }
    }

    @GetMapping(value = "/initFromFile", produces = "application/json")
    public ResponseEntity<String> initFromFile(@RequestParam(name = "path") final String path) throws IOException {
        openPropService.initDBFromFile(path);
        return ResponseEntity.status(OK).body("Completed");
    }
}
