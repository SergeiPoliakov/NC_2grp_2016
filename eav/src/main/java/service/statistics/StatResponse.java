package service.statistics;

/**
 * Created by Hroniko on 30.03.2017.
 */
// Класс для хранения одной точки статистики, помещаемой в массив-ответ на запрос тегов (AJAX)
public class StatResponse {

    private Integer id; // служебное поле, вдруг понадобиться на стороне клиента отсортировать по возрастанию элементы коллекции перед выводом на график / диаграмму

    private Double nkey; // Числовой ключ
    private String  skey; // Строковый ключ (все ради универсальности)

    private Double nvalue; // Числовое значение
    private String  svalue; // Строковое значение

    private String date; // Дата создания в текстовом виде

    private String info; // Вспомогательное текстовое поле

    // Вспомогательные поля (для

    private String plotview;


    public StatResponse() {
    }

    public StatResponse(Double nkey, Double nvalue) {
        this.nkey = nkey;
        this.nvalue = nvalue;
    }

    public StatResponse(String skey, Double nvalue) {
        this.skey = skey;
        this.nvalue = nvalue;
    }

    public StatResponse(String skey, String svalue) {
        this.skey = skey;
        this.svalue = svalue;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Double getNkey() {
        return nkey;
    }

    public void setNkey(Double nkey) {
        this.nkey = nkey;
    }

    public String getSkey() {
        return skey;
    }

    public void setSkey(String skey) {
        this.skey = skey;
    }

    public Double getNvalue() {
        return nvalue;
    }

    public void setNvalue(Double nvalue) {
        this.nvalue = nvalue;
    }

    public String getSvalue() {
        return svalue;
    }

    public void setSvalue(String svalue) {
        this.svalue = svalue;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getInfo() {
        return info;
    }

    public void setInfo(String info) {
        this.info = info;
    }
}
