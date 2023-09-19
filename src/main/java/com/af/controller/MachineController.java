package com.af.controller;

import com.af.domain.Machine;
import org.apache.commons.math.MathException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.ArrayList;
import java.util.List;

@RestController
public class MachineController {

    private static final Logger LOGGER = LoggerFactory.getLogger(MachineController.class);

    @GetMapping("/getOutliers")
    public List<Machine> getOutliers(@RequestBody List<Machine> machines, @RequestParam double threshold) {
        try {
             return findOutliers(machines, threshold);
        }catch (MathException m){
            m.printStackTrace();
        }
        return null;
    }

    private List<Machine> findOutliers(List<Machine> machines, double threshold) throws MathException {
        int sum = 0, index = 0;
        List<Machine> outliers = new ArrayList<>();
        int numberOfMachines = machines.size();
        double agesArray[] = new double[numberOfMachines];

        for(Machine machine : machines){
            int machineAge = Integer.parseInt(machine.getAge().split(" ")[0]);
            agesArray[index] = machineAge;
            sum += machineAge;
            index++;
        }

        double mean = sum / numberOfMachines;
        LOGGER.info("Mean: " + mean);
        LOGGER.info("===================\n\n");
        double standardDeviation = getStandardDeviation(agesArray);
        LOGGER.info("Standard deviation: "+standardDeviation);

        for (Machine machine : machines) {
            int machineAge = Integer.parseInt(machine.getAge().split(" ")[0]);
            LOGGER.info("Age: " + machineAge);
            double zScore = (machineAge - mean) / standardDeviation;
            if (zScore > threshold){
                outliers.add(machine);
            }
            LOGGER.info("ZScore: " + zScore + "\n\n");
        }
        return outliers;
    }

    private double getStandardDeviation(double numArray[])
    {
        double sum = 0.0, standardDeviation = 0.0;
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