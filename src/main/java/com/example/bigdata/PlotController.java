package com.example.bigdata;

import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Function;

@RestController
@RequiredArgsConstructor
public class PlotController {

    private int currentIndex = 0;
    private boolean completed = false;
    private double[] values;

    private final Function<DataHolder, String> plotFunction;

    @PostConstruct
    public void initValues(){
        String filePath = "src/main/resources/swe307_pro1.csv";
        int targetColumn = 12;
        values = getValues(filePath, targetColumn);
    }

    @RequestMapping(value = "/plot", produces = "image/svg+xml")
    public ResponseEntity<String> load() {
        if (completed) {
            return new ResponseEntity<>(HttpStatus.NO_CONTENT);
        }

        HttpHeaders responseHeaders = new HttpHeaders();
        responseHeaders.set("Refresh", "1");

        double value;
        if (currentIndex < values.length) {
            value = values[currentIndex];
            currentIndex++;
        } else {
            completed = true;
            return new ResponseEntity<>(HttpStatus.NO_CONTENT);
        }

        String svg;
        synchronized (plotFunction) {
            svg = plotFunction.apply(new DataHolder(value));
        }

        return new ResponseEntity<>(svg, responseHeaders, HttpStatus.OK);
    }

    public double[] getValues(String filePath, int targetColumn) {
        List<Double> valuesList = new ArrayList<>();
        boolean skipFirstLine = true;

        try (BufferedReader bufferedReader = new BufferedReader(new FileReader(filePath))) {
            String line;
            while ((line = bufferedReader.readLine()) != null) {
                if (skipFirstLine) {
                    skipFirstLine = false;
                    continue;
                }
                String[] values = line.split(",");
                if (values.length > targetColumn) {
                    try {
                        double value = Double.parseDouble(values[targetColumn]);
                        valuesList.add(value);
                    } catch (NumberFormatException e) {
                        System.err.println("Invalid Double Value: " + values[targetColumn]);
                    }
                }
            }
        } catch (IOException e) {
            System.err.println("Reading File Exception: " + e.getMessage());
        }

        return valuesList.stream().mapToDouble(Double::doubleValue).toArray();
    }
}
