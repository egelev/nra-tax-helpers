package com.egelev.nra;

import com.egelev.nra.gateways.tr212.impl.CsvParserImpl;
import com.egelev.nra.gateways.tr212.CsvRecord;
import java.net.URISyntaxException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class NraApplication {

	public static void main(String[] args) throws URISyntaxException {
		SpringApplication.run(NraApplication.class, args);
	}

}
