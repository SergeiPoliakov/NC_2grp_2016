package entities;
import java.util.ArrayList;

/**
 * Created by Hroniko on 22.03.2017.
 */
// Класс узла нагруженного дерева тегов (хранит одну букву и кучу ссылок)
public class TagNode  extends BaseEntitie {
    public static final int objTypeID = 1010; // Тип сущности

    private Integer id; // Ключ - это айдишник нода  (выступает в роли ключа)
    private Integer type_id = objTypeID; // Тип сущности
    private String name; // имя сущности

    // парметры с номерами
    private char value; // 701 // Значение (буква)
    private int usage_count; // 702 // Число использования // Количество использований данного тега (сколько юзеров привесили этот тег) // Если ноль, то это не основной нод, к нему не вешаются юзеры



    // ссылки с номерами
    private TagNode root; // 703  ссылка на родителя
    private ArrayList<TagNode> parents; // 704 // лист ссылок на другие узлы тегов
    private ArrayList<Integer> users; // 705 // лист ids юзеров, для которых этот нод является конечным для заданного тега



    // Конструктор узла:
    public TagNode() {
        this.root = null;
        this.parents = new ArrayList<>();
        this.users = new ArrayList<>();
        this.usage_count = 0;
        this.name = "";
    }

    // Конструктор узла:
    public TagNode(TagNode root, char value) {
        this.root = root;
        this.value = value;
        this.parents = new ArrayList<>();
        this.users = new ArrayList<>();
        this.usage_count = 0;
        this.name = "";
    }


    @Override
    public String toString() { // Приведение к строке в виде {ключ:значение}
        return "{" + this.id + ":" + this.value + "}";
    }

    public void addUserId(Integer user_id){
        if(! this.users.contains(user_id)){
            this.users.add(user_id);
            this.usage_count ++;
            System.out.println("Добавили юзера " + user_id);
        }

        /*this.users.add(user_id);
        this.usage_count ++;
        System.out.println("-------------------->>>>> Добавили юзера " + user_id);
        */
    }

    public static int getObjTypeID() {
        return objTypeID;
    }

    public Integer getType_id() {
        return type_id;
    }

    public void setType_id(Integer type_id) {
        this.type_id = type_id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setParents(ArrayList<TagNode> parents) {
        this.parents = parents;
    }

    public TagNode getRoot() {
        return root;
    }

    public void setRoot(TagNode root) {
        this.root = root;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public char getValue() {
        return value;
    }

    public void setValue(char value) {
        this.value = value;
    }

    public ArrayList<TagNode> getParents() {
        return parents;
    }

    public TagNode getParents(Integer position) {
        TagNode tagNode = null;
        if(parents.size()> position){ // если позиция в диапазоне размера списка, то
           tagNode =  parents.get(position); //  вытаскиваем и отдаем
        }
        return tagNode;
    }

    public void setParents(TagNode parent) {
        if(! this.parents.contains(parent)){
            this.parents.add(parent);
        }
    }

    public ArrayList<Integer> getUsers() {
        return users;
    }

    public Integer getUsers(Integer position) {
        if (this.usage_count > position){
            return this.users.get(position);
        }
        return null;
    }

    public void setUsers(ArrayList<Integer> users) {
        this.users = users;
    }

    public void setUsers(Integer user) {
        if(! this.users.contains((Object) user)){
            this.users.add(user);
        }
    }

    public void delUsers(Integer user) {
        if(this.users.contains((Object) user)){
            this.users.remove((Object) user);
            this.usage_count--;
        }
    }

    public int getUsage_count() {
        return usage_count;
    }

    public void setUsage_count(int usage_count) {
        this.usage_count = usage_count;
    }
}
