package com.af.controller;

import com.af.domain.Machine;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

@RestController
public class MachineController {

    private static final Logger LOGGER = LoggerFactory.getLogger(MachineController.class);
    public static final String SPACE = " ";
    public static final String DAYS = "days";
    public static final String MONTHS = "months";
    public static final String YEARS = "years";
    public static final int ZERO_VALUE = 0;

    @GetMapping("/outliers")
    public List<Machine> getOutliers(@RequestBody List<Machine> machines, @RequestParam double threshold) {
        try {
             return findOutliers(machines, threshold);
        }catch (Exception ex){
            ex.printStackTrace();
        }
        return Collections.emptyList();
    }

    private List<Machine> findOutliers(List<Machine> machines, double threshold) {
        AtomicInteger counter = new AtomicInteger(ZERO_VALUE);
        List<Machine> outliers = new ArrayList<>();
        int numberOfMachines = machines.size();
        double[] machineAgesArray = new double[numberOfMachines];

        // calculating sum loping over stream of machines
        double sum = machines.stream().mapToDouble(machine -> Double.parseDouble(machine.getAge().split(SPACE)[ZERO_VALUE])).sum();
        LOGGER.info(String.format("SUM: %s", sum));


        // looping over machines to determine machine ages (assuming 'days' are the standard age unit)
        machines.stream().forEach(machine -> {
            String[] machineValArray = machine.getAge().split(SPACE);
            int machineAge = Integer.parseInt(machineValArray[ZERO_VALUE].trim());
            String durationUnit = machineValArray[1].trim(); // Assuming 'days' as the default age unit
            switch(durationUnit)
            {
                case DAYS:
                    break;

                case MONTHS:
                    machineAge = machineAge * 30; // approximate conversion to days from months
                    break;

                case YEARS:
                    machineAge = machineAge * 365; // approximate conversion to days from years
                    break;

                default :
            }
            machineAgesArray[counter.getAndIncrement()] = machineAge;
        });

        double mean = sum / numberOfMachines;
        LOGGER.info(String.format("MEAN: %s", mean));
        LOGGER.info("===================\n\n");
        double standardDeviation = getStandardDeviation(machineAgesArray);
        LOGGER.info(String.format("STANDARD DEVIATION: %s", standardDeviation));

        // resetting counter to 0 before reusing it
        counter.set(ZERO_VALUE);

        // calculating Z-score and finding outliers
        machines.stream().forEach(machine -> {
            double machineAge = machineAgesArray[counter.getAndIncrement()];
            LOGGER.info(String.format("MACHINE AGE: %s", machineAge));
            double zScore = (machineAge - mean) / standardDeviation;
            if (zScore > threshold){
                outliers.add(machine);
            }
            LOGGER.info(String.format("ZScore: %s", zScore + "\n\n"));
        });
        return outliers;
    }

    // method to calculate standard deviation
    private double getStandardDeviation(double[] numArray)
    {
        double sum = 0.0;
        double standardDeviation = 0.0;
        int length = numArray.length;
        for(double num : numArray) {
            sum += num;
        }
        double mean = sum/length;
        for(double num: numArray) {
            standardDeviation += Math.pow(num - mean, 2);
        }
        return Math.sqrt(standardDeviation/length);
    }
}