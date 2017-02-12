package web;

/**
 * Created by Hroniko on 11.02.2017.
 */

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.sql.SQLException;


import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartFile;
import service.UploadServiceImp;
import service.UserServiceImp;


@Controller
public class FileUploadController {

    private static final Logger logger = LoggerFactory
            .getLogger(FileUploadController.class);

    private UploadServiceImp uploadService = UploadServiceImp.getInstance();

    @RequestMapping("/upload")
    public String uploadPage() throws Exception {
        return "upload";
    }

    @RequestMapping("/uploadMultiple")
    public String uploadMultiplePage() throws Exception {
        return "uploadMultiple";
    }

    /*
    @RequestMapping("/uploadAvatar")
    public String uploadAvatar() throws Exception {
        return "profile";
    }
    */

    // Загрузка одного файла:
    @RequestMapping(value = "/uploadFile", method = RequestMethod.POST)
    @ResponseBody
    public String uploadFileHandler(@RequestParam("name") String name,
                             @RequestParam("file") MultipartFile file) {

        if (!file.isEmpty()) {
            try {
                byte[] bytes = file.getBytes();
                System.out.println(file.getSize());

                // Проверяем есть ли такая папка и создаем ее, если нет
                String rootPath = System.getProperty("catalina.home");
                File dir = new File(rootPath + File.separator + "tmpFiles");
                if (!dir.exists())
                    dir.mkdirs();

                // Создаем файл на сервере
                File serverFile = new File(dir.getAbsolutePath()
                        + File.separator + name);
                BufferedOutputStream stream = new BufferedOutputStream(
                        new FileOutputStream(serverFile));
                stream.write(bytes);
                stream.close();

                logger.info("Server File Location="
                        + serverFile.getAbsolutePath());

                return "Ваш файл успешно загружен =" + name;
            } catch (Exception e) {
                return "Неудачная загружка файла " + name + " = " + e.getMessage();
            }
        } else {
            return "Неудачная загрузка " + name
                    + " - файл пуст.";
        }
    }

    // Многофайловая загрузка:
    @RequestMapping(value = "/uploadMultipleFile", method = RequestMethod.POST)
    @ResponseBody
    public String uploadMultipleFileHandler(@RequestParam("name") String[] names,
                                     @RequestParam("file") MultipartFile[] files) {

        if (files.length != names.length)
            return "Отсутствует обязательная инфа";

        String message = "";
        for (int i = 0; i < files.length; i++) {
            MultipartFile file = files[i];
            String name = names[i];
            try {
                byte[] bytes = file.getBytes();

                // Проверяем есть ли такая папка и создаем ее, если нет
                String rootPath = System.getProperty("catalina.home");
                File dir = new File(rootPath + File.separator + "tmpFiles");
                if (!dir.exists())
                    dir.mkdirs();

                // Создаем файл на сервере
                File serverFile = new File(dir.getAbsolutePath()
                        + File.separator + name);
                BufferedOutputStream stream = new BufferedOutputStream(
                        new FileOutputStream(serverFile));
                stream.write(bytes);
                stream.close();

                logger.info("Расположение файла на сервере ="
                        + serverFile.getAbsolutePath());

                message = message + "Загрузка файлов выполнена успешно = " + name
                        + "=>";
            } catch (Exception e) {
                return "Неудачная загрузка файлов " + name + " => " + e.getMessage();
            }
        }
        return message;
    }


    // Загрузка аватара
    @RequestMapping(value = "/uploadAvatar", method = RequestMethod.POST)
    //@ResponseBody // Пришлось убрать, так как иначе не перенаправляет на другую страницу
    public String uploadAvatarHandler(@RequestParam("file") MultipartFile file) throws SQLException {

        if (!file.isEmpty()) {
            try {
                // Получаем id текущего пользователя для формирования пути к его папке с файлами
                Integer currentUserId = new UserServiceImp().getCurrentUser().getId();

                // Получаем расширение файла
                // System.out.println(file.getOriginalFilename()); // Для отладки
                String[] extFile = file.getOriginalFilename().split("\\.");
                // System.out.println(extFile[extFile.length-1]); // Для отладки

                // Формируем имя файла, пример: avatar_10005.png
                String name = "avatar_" + currentUserId + "." + extFile[extFile.length-1];


                byte[] bytes = file.getBytes();
                //System.out.println(file.getSize());

                /* Проверяем есть ли такая папка (и создаем ее, если ее нет)  C:/ALL/apache-tomcat-9.0.0.M15/upload/10005/avatar*/
                String rootPath = ""; //System.getProperty("catalina.home");
                String relativePatchToFolder = File.separator + "upload" + File.separator + currentUserId  + File.separator + "avatar";
                File dir = new File(rootPath + relativePatchToFolder);
                if (!dir.exists())
                    dir.mkdirs();
                else{
                    // иначе, если папка есть, удаляем в ней все файлы, которые уже не нужны (старый аватар)
                    for (File oldFile : new File(rootPath + relativePatchToFolder).listFiles())
                        if (oldFile.isFile()) oldFile.delete();
                }


                // Создаем файл на сервере
                File serverFile = new File(dir.getAbsolutePath()
                        + File.separator + name);
                BufferedOutputStream stream = new BufferedOutputStream(
                        new FileOutputStream(serverFile));
                stream.write(bytes);
                stream.close();

                logger.info("Файл загружен по адресу " + serverFile.getAbsolutePath());
                System.out.println("Файл загружен по адресу " + serverFile.getAbsolutePath());


                // Выполняем обновление ссылки в базе:
                String fullPatchToFolder = rootPath + relativePatchToFolder;
                String fullPatchToFile = fullPatchToFolder + File.separator + name;
                uploadService.updateAvatar(currentUserId, serverFile.getAbsolutePath());






                //return "profile";
            } catch (Exception e) {
                //return "profile"; // return "Неудачная загрузка файла: " + e.getMessage(); // надо потом реализовать вывод ошибки во всплывающем сообщении или перенаправление на страницу ошибки
            }
        } else {
            //return "profile"; // return "Неудачная загрузка файла: файл пуст"; // надо потом реализовать вывод ошибки во всплывающем сообщении
        }
        return "redirect:/profile";
    }


}