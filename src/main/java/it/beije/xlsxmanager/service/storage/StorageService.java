package it.beije.xlsxmanager.service.storage;

import it.beije.xlsxmanager.exception.StorageException;
import org.springframework.core.io.Resource;
import org.springframework.web.multipart.MultipartFile;

import java.nio.file.Path;
import java.util.stream.Stream;

public interface StorageService {

	void init();

	Path store(MultipartFile file) throws StorageException;

	Stream<Path> loadAll();

	Path load(String filename);

	Resource loadAsResource(String filename);

	void deleteAll();

	Path getPathResources();

}
