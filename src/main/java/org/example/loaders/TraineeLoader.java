package org.example.loaders;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import lombok.extern.slf4j.Slf4j;
import org.example.models.Trainee;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.PropertySource;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
@Component
public class TraineeLoader implements Loader<Trainee> {
    private String filePath;
    private Map<Long, Trainee> trainees;
    @Autowired
    public TraineeLoader(@Qualifier("traineesFilePath") String  filePath, @Qualifier("traineeStorage") Map<Long, Trainee> trainees) {
        this.filePath = filePath;
        this.trainees = trainees;
    }
    @PostConstruct
    public void init() {
        load();
    }
    @PreDestroy
    public void destroy() {
        try{
            ObjectMapper mapper = new ObjectMapper();
            mapper.registerModule(new JavaTimeModule());
            List<Trainee> traineeList=trainees.values().stream().toList();
            mapper.writeValue(new File(filePath), traineeList);
        }
        catch (Exception e){
            e.printStackTrace();
        }
    }
    @Override
    public Map<Long, Trainee> load() {
        try{
            ObjectMapper objectMapper = new ObjectMapper();
            objectMapper.registerModule(new JavaTimeModule());
            List<Trainee> traineesList = objectMapper.readValue(new File(filePath), new TypeReference<List<Trainee>>() {});
            Map<Long, Trainee> map = this.trainees;
            for(Trainee trainee : traineesList){
                map.put(trainee.getUserId(), trainee);
            }
            return map;
        }
        catch (IOException e){
            e.printStackTrace();
        }
        return new HashMap<>();
    }

    @Override
    public void clear() {
        try (FileWriter writer = new FileWriter(new File(filePath))) {
            this.trainees.clear();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
