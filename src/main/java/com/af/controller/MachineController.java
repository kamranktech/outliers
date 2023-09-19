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

    @GetMapping("/getOutliers")
    public List<Machine> getOutliers(@RequestBody List<Machine> machines, @RequestParam double threshold) {
        try {
             return findOutliers(machines, threshold);
        }catch (Exception ex){
            ex.printStackTrace();
        }
        return Collections.emptyList();
    }

    private List<Machine> findOutliers(List<Machine> machines, double threshold) {
        AtomicInteger counter = new AtomicInteger(0);
        List<Machine> outliers = new ArrayList<>();
        int numberOfMachines = machines.size();
        double[] machineAgesArray = new double[numberOfMachines];

        double sum = machines.stream().mapToDouble(machine1 -> Double.parseDouble(machine1.getAge().split(" ")[0])).sum();
        LOGGER.info("SUM: "+sum);


        machines.stream().forEach(machine -> {
            String[] machineValArray = machine.getAge().split(" ");
            int machineAge = Integer.parseInt(machineValArray[0].trim());
            String durationUnit = machineValArray[1].trim(); // Assuming 'days' as the default age unit
            switch(durationUnit)
            {
                case "days" :
                    break;

                case "months" :
                    machineAge = machineAge * 30; // approximate conversion to days from months
                    break;

                case "years" :
                    machineAge = machineAge * 365; // approximate conversion to days from years
                    break;

                default :
            }
            machineAgesArray[counter.getAndIncrement()] = machineAge;
        });

        double mean = sum / numberOfMachines;
        LOGGER.info(String.format("Mean: %s", mean));
        LOGGER.info("===================\n\n");
        double standardDeviation = getStandardDeviation(machineAgesArray);
        LOGGER.info(String.format("Standard deviation: %s", standardDeviation));

        // resetting counter to 0 before reusing it
        counter.set(0);
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