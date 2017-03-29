package service.tags;

import entities.TagNode;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.stereotype.Component;

/**
 * Created by Hroniko on 29.03.2017.
 */
@Component
@EnableScheduling
public class RootNode {
    private static final TagNode root = new TagNode(); // Базовый нод

    public RootNode() {
        root.setId(90001);
        root.setName("ROOT_NODE");
    }

    public static TagNode getRoot() {
        return root;
    }
}
