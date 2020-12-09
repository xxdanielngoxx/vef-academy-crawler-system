package io.vef.academy.common.events;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;
import seed.SeedUrl;

import java.io.Serializable;
import java.util.Collections;
import java.util.List;

@Getter
@ToString
@NoArgsConstructor
@AllArgsConstructor
public class TaskExecutedEvent implements Serializable {

    private static final long serialVersionUID = -5126942079363310600L;

    private static final String FROM = "TRACKING-SERVICE";

    private String taskId;

    private List<SeedUrl> seedUrlList;

    private Metadata metadata;

    private TaskExecutedEvent(String taskId, List<SeedUrl> seedUrlList) {
        this.taskId = taskId;
        this.seedUrlList = Collections.unmodifiableList(seedUrlList);
        this.metadata = Metadata.of(FROM);
    }

    public static TaskExecutedEvent of(String taskId, List<SeedUrl> seedUrlList) {
        return new TaskExecutedEvent(taskId, seedUrlList);
    }
}
