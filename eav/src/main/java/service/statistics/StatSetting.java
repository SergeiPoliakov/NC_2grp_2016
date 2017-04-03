package service.statistics;

/**
 * Created by Hroniko on 01.04.2017.
 */
// Класс для пересылки в JSON на страницу статистики настройки выводимой статистики
public class StatSetting {

    private Integer id; // служебное поле, вдруг понадобиться на стороне клиента отсортировать по возрастанию элементы коллекции перед выводом на график / диаграмму

    private String plotview; // Вид диаграммы

    private String state; // Состояние диаграммы - on | off

    private String datatype; // Тип данных

    private String period; // Период

    private String location_id; // Расположение на странице


    // Доп параметры

    private String xlabel; // Подпись осей по ОХ

    private String ylabel; // Подпись осей по ОУ

    private String title; // Подпись диаграммы (название диаграммы)


    public StatSetting() {
    }


    public StatSetting(String plotview, String state) {
        this.plotview = plotview;
        this.state = state;
    }

    public StatSetting(String plotview, String state, String datatype, String period, String location_id) {
        this.plotview = plotview;
        this.state = state;
        this.datatype = datatype;
        this.period = period;
        this.location_id = location_id;
    }

    public StatSetting(String plotview, String state, String datatype, String period, String location_id, String title) {
        this.plotview = plotview;
        this.state = state;
        this.datatype = datatype;
        this.period = period;
        this.location_id = location_id;
        this.title = title;
    }

    public StatSetting(String plotview, String state, String datatype, String period, String location_id, String xlabel, String ylabel) {
        this.plotview = plotview;
        this.state = state;
        this.datatype = datatype;
        this.period = period;
        this.location_id = location_id;
        this.xlabel = xlabel;
        this.ylabel = ylabel;
    }

    public StatSetting(String plotview, String state, String datatype, String period, String location_id, String title, String xlabel, String ylabel) {
        this.plotview = plotview;
        this.state = state;
        this.datatype = datatype;
        this.period = period;
        this.location_id = location_id;
        this.title = title;
        this.xlabel = xlabel;
        this.ylabel = ylabel;
    }



    ///////


    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getPlotview() {
        return plotview;
    }

    public void setPlotview(String plotview) {
        this.plotview = plotview;
    }

    public String getState() {
        return state;
    }

    public void setState(String state) {
        this.state = state;
    }

    public String getDatatype() {
        return datatype;
    }

    public void setDatatype(String datatype) {
        this.datatype = datatype;
    }

    public String getPeriod() {
        return period;
    }

    public void setPeriod(String period) {
        this.period = period;
    }

    public String getLocation_id() {
        return location_id;
    }

    public void setLocation_id(String location_id) {
        this.location_id = location_id;
    }

    public String getXlabel() {
        return xlabel;
    }

    public void setXlabel(String xlabel) {
        this.xlabel = xlabel;
    }

    public String getYlabel() {
        return ylabel;
    }

    public void setYlabel(String ylabel) {
        this.ylabel = ylabel;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }
}
