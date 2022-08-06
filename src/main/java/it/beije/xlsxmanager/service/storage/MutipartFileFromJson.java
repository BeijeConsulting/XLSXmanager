/**
 * @author Giuseppe Raddato
 * Data: 05 ago 2022
 */
package it.beije.xlsxmanager.service.storage;

import lombok.extern.slf4j.Slf4j;
import org.springframework.web.multipart.MultipartFile;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
@Slf4j
public class MutipartFileFromJson implements MultipartFile {

    private byte[] fileJson;
    private String name;

    public MutipartFileFromJson(byte[] dataJson, String name) {
        log.debug("entro nella classe multipartJSON");
        this.fileJson = dataJson;
        this.name = name;
    }

    @Override
    public String getName() {
        return name + ".json";
    }

    @Override
    public String getOriginalFilename() {
        return name + ".json";
    }

    @Override
    public String getContentType() {
        return "json";
    }

    @Override
    public boolean isEmpty() {
        return fileJson.length == 0;
    }

    @Override
    public long getSize() {
        return fileJson.length;
    }

    @Override
    public byte[] getBytes() throws IOException {
        return fileJson;
    }

    @Override
    public InputStream getInputStream() throws IOException {
        return new ByteArrayInputStream(fileJson);
    }

    @Override
    public void transferTo(File dest) throws IOException, IllegalStateException {
        log.debug("trasfert");
    }

}
