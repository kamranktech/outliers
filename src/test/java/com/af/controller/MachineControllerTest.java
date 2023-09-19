package com.af.controller;

import com.af.domain.Machine;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import java.util.ArrayList;
import java.util.List;

@ContextConfiguration(classes = {MachineController.class})
@RunWith(SpringJUnit4ClassRunner.class)
public class MachineControllerTest {

    @Autowired
    private MachineController machineController;

    @Test
    public void validateRequest(){
        List<Machine> machines = new ArrayList<>();
        machines.add(new Machine(1L, "10 days"));
        machines.add(new Machine(1L, "20 days"));
        machines.add(new Machine(1L, "30 years"));
        machines.add(new Machine(1L, "40 days"));
        machines.add(new Machine(1L, "50 months"));






    }

}

