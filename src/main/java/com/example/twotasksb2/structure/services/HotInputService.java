package com.example.twotasksb2.structure.services;

import com.example.twotasksb2.structure.entities.HotInput;
import com.example.twotasksb2.structure.repositories.DictTaskRepository;
import com.example.twotasksb2.structure.repositories.HotInputRepository;
import com.example.twotasksb2.tasks.TaskEnum;
import com.example.twotasksb2.utils.AdapterUtils;
import com.example.twotasksb2.utils.TaskPojo;
import com.example.twotasksb2.utils.pojos.Option;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 *  Service for interactions with inputs
 */
@Service
@AllArgsConstructor
public class HotInputService {
    private final HotInputRepository hotInputRepository;
    private final DictTaskRepository dictTaskRepository;
    private final TaskService taskService;

    /**
     * Create
     */
    public HotInput createAndSave(Long taskCode, String input){
        return hotInputRepository.save(new HotInput(dictTaskRepository.findByCode(taskCode), input));
    }

    /**
     * Get input options (Option) by it's task
     */
    public List<Option> getOptionsByTaskCode(Long taskCode){
        return hotInputRepository.findAllByTask(dictTaskRepository.findByCode(taskCode)).stream()
                .map(t -> new Option(t.getId(), AdapterUtils.beautyVersion(t.getInput(), taskCode))).collect(Collectors.toList());
    }

    /**
     * Delete
     */
    public void delete(Long id){
        hotInputRepository.delete(hotInputRepository.findById(id).get());
    }

    /**
     *  Get answer for chosen input
     */
    public String calculate(Long inputId) {
        Optional<HotInput> option = hotInputRepository.findById(inputId);
        if (option.isEmpty()) return null;
        TaskPojo pojo = extractPojo(option.get());
        return "Input:\n"
                + AdapterUtils.beautyVersion(pojo)
                + "\n\nResult:\n"
                + taskService.calculate(option.get().getTask().getCode(), pojo);
    }

    /**
     * Transform input to POJO which is usable for calculations
     */
    public TaskPojo extractPojo(HotInput input){
        String json = input.getInput();
        if (TaskEnum.ARRAYS.getCode().equals(input.getTask().getCode())) return AdapterUtils.getPojoForTaskOne(json);
        if (TaskEnum.MAGIC_SQUARE.getCode().equals(input.getTask().getCode())) return AdapterUtils.getPojoForTaskTwo(json);
        return null;
    }
}
