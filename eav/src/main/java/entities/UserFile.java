package entities;

import java.util.ArrayList;

/**
 * Created by Hroniko on 12.03.2017.
 */
// Класс для хранения информации о пользовательских файлах
public class UserFile extends BaseEntitie {

    public static final Integer objTypeID = 1009; // Тип сущности

    private Integer id; // Айдишник файла как объекта в базе
    private Integer type_id = objTypeID; // Тип сущности
    private String name; // имя сущности (не то имя, что на самом деле имеет оригинальный файл, а имя сущности в базе)

    // парметры с номерами (800 ... 899)
    private String date_create; // 801 // Дата создания (загрузки на сервер)
    private String date_change; // 802 // Дата изменения имени
    private String date_download; // 803 // Дата последнего скачиввания
    private String name_original; // 804 // Исходное имя файла
    private String name_current; // 805 // Имя файла на сервере
    private String extension; // 806 // Расширение файла
    private String puth; // 807 // Путь к файлу на серверу
    private Long size; // 808 // Размер файла в mb
    private Long count_download; // 809 // Количество скачиваний
    private String share = "private"; // 810 // Флаг расшаренности, "private" если закрытая ссылка, "public" - если файл расшарен и его могут использовать другие юзеры (это чтобы нельзя было удалить расшаренный файл, а просто убрать ссылку на него у текущего юзера)
    // По сути, тогда такие расшаренные файлы надо отдавать систем-юзеру позже посмотрю, удобно ли это, а пока так
    // Поэтому, пока не отдали файл систем-юзеру, оставляем его у текущего юзера, просто, если он его удаляет, а файл расшарен, ставим флаг удаления в "":
    private String exist = "yes"; // 811 // Флаг наличия текущего файла у данного юзера-родителя ("yes" - если существует и "no" - если удален)



    // ссылки с номерами
    private Integer holder_id; // 812  ссылка на владельца файла (это может быть как юзер, так и систем-юзер, так и встреча или диалог)


    public UserFile() {
    }


    public static Integer getObjTypeID() {
        return objTypeID;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
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

    public String getDate_create() {
        return date_create;
    }

    public void setDate_create(String date_create) {
        this.date_create = date_create;
    }

    public String getDate_change() {
        return date_change;
    }

    public void setDate_change(String date_change) {
        this.date_change = date_change;
    }

    public String getDate_download() {
        return date_download;
    }

    public void setDate_download(String date_download) {
        this.date_download = date_download;
    }

    public String getName_origanal() {
        return name_original;
    }

    public void setName_origanal(String name_origanal) {
        this.name_original = name_origanal;
    }

    public String getName_current() {
        return name_current;
    }

    public void setName_current(String name_current) {
        this.name_current = name_current;
    }

    public String getExtension() {
        return extension;
    }

    public void setExtension(String extension) {
        this.extension = extension;
    }

    public String getPuth() {
        return puth;
    }

    public void setPuth(String puth) {
        this.puth = puth;
    }

    public Long getSize() {
        return size;
    }

    public void setSize(Long size) {
        this.size = size;
    }

    public Long getCount_download() {
        return count_download;
    }

    public void setCount_download(Long count_download) {
        this.count_download = count_download;
    }

    public String getShare() {
        return share;
    }

    public void setShare(String share) {
        this.share = share;
    }

    public String getExist() {
        return exist;
    }

    public void setExist(String exist) {
        this.exist = exist;
    }

    public Integer getHolder_id() {
        return holder_id;
    }

    public void setHolder_id(Integer holder_id) {
        this.holder_id = holder_id;
    }
}
