package com.af.controller;

import com.af.domain.Machine;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import java.util.ArrayList;
import java.util.List;

import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.user;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@RunWith(SpringRunner.class)
@SpringBootTest
@AutoConfigureMockMvc(printOnlyOnFailure = false)
public class MachineControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Test
    public void testIfOneOutlierFromGivenList() throws Exception {
        List<Machine> machines = new ArrayList<>();
        machines.add(new Machine(1L, "10 days"));
        machines.add(new Machine(2L, "20 days"));
        machines.add(new Machine(3L, "30 years"));
        machines.add(new Machine(4L, "40 days"));
        machines.add(new Machine(5L, "50 days"));


        MvcResult result = mockMvc.perform(MockMvcRequestBuilders
                        .get("/outliers")
                        .with(user("admin").password("admin"))
                        .param("threshold", "2")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(new ObjectMapper().writeValueAsString(machines))
                        .accept(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isOk())
                .andReturn();

        ObjectMapper mapper = new ObjectMapper();
        List<Machine> actual = mapper.readValue(result.getResponse().getContentAsString(), new TypeReference<List<Machine>>() {
        });
        Assert.assertEquals(1, actual.size());
    }

    @Test
    public void testNoOutliersFromGivenList() throws Exception {
        List<Machine> machines = new ArrayList<>();
        machines.add(new Machine(1L, "10 days"));
        machines.add(new Machine(2L, "20 days"));
        machines.add(new Machine(3L, "30 days"));
        machines.add(new Machine(4L, "40 days"));
        machines.add(new Machine(5L, "50 days"));


        MvcResult result = mockMvc.perform(MockMvcRequestBuilders
                        .get("/outliers")
                        .param("threshold", "2")
                        .with(user("admin").password("admin"))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(new ObjectMapper().writeValueAsString(machines))
                        .accept(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isOk())
                .andReturn();

        ObjectMapper mapper = new ObjectMapper();
        List<Machine> actual = mapper.readValue(result.getResponse().getContentAsString(), new TypeReference<List<Machine>>() {
        });
        Assert.assertEquals(0, actual.size());
    }

    @Test
    public void testIfBadRequestWhenThresholdNotSet() throws Exception {
        List<Machine> machines = new ArrayList<>();
        machines.add(new Machine(1L, "10 days"));
        machines.add(new Machine(2L, "20 days"));
        machines.add(new Machine(3L, "30 days"));
        machines.add(new Machine(4L, "40 days"));
        machines.add(new Machine(5L, "50 days"));

        mockMvc.perform(MockMvcRequestBuilders
                        .get("/outliers")
                        .with(user("admin").password("admin"))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(new ObjectMapper().writeValueAsString(machines))
                        .accept(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isBadRequest());
    }

    @Test
    public void testIfRequestUnAuthorisedWhenUserLoginDetailsNotGiven() throws Exception {
        List<Machine> machines = new ArrayList<>();
        machines.add(new Machine(1L, "10 days"));
        machines.add(new Machine(2L, "20 days"));
        machines.add(new Machine(3L, "30 days"));
        machines.add(new Machine(4L, "40 days"));
        machines.add(new Machine(5L, "50 days"));

        mockMvc.perform(MockMvcRequestBuilders
                        .get("/outliers")
                        .param("threshold", "2")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(new ObjectMapper().writeValueAsString(machines))
                        .accept(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isUnauthorized());
    }

}

