package com.vi.openprop.service;

import com.vi.openprop.service.persistor.DataPersistor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

@Service
public class OpenPropService {

    private DataPersistor dataPersistor;

    @Autowired
    public OpenPropService(DataPersistor dataPersistor) {
        this.dataPersistor = dataPersistor;
    }

    public void initDBFromFile(String filePath) throws IOException {
        String response = Files.readString(Path.of(filePath));
        dataPersistor.persistUraDataToDB(response,0);
    }

}
