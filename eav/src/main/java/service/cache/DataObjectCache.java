package service.cache;

import java.lang.reflect.InvocationTargetException;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.concurrent.TimeUnit;
import com.google.common.cache.CacheBuilder;
import com.google.common.cache.CacheLoader;
import com.google.common.cache.LoadingCache;
import com.google.common.util.concurrent.ListenableFuture;
import entities.DataObject;
import service.LoadingServiceImp;

/**
 * Created by Lawrence on 18.02.2017.
 */
public class DataObjectCache {

    private static LoadingCache<Integer, DataObject> doCache;

    private static LoadingServiceImp loadingService = new LoadingServiceImp();

    static {
        doCache = CacheBuilder.newBuilder()
                .maximumSize(100)
                .expireAfterWrite(5, TimeUnit.MINUTES)
                .build(
                        new CacheLoader<Integer, DataObject>() {

                            @Override
                            public DataObject load(Integer key) throws Exception {
                                return getDataObjectById(key);
                            }

                            @Override
                            public ListenableFuture<DataObject> reload(Integer key, DataObject oldValue) throws Exception {
                                return super.reload(key, oldValue);
                            }

                            @Override
                            public Map<Integer, DataObject> loadAll(Iterable<? extends Integer> keys) throws Exception {
                                return getListDataObjectById(keys);
                            }
                        }
                );
    }

    public static LoadingCache<Integer, DataObject> getLoadingCache() {
        return doCache;
    }

    public static DataObject getDataObjectById(int id) throws InvocationTargetException, SQLException, IllegalAccessException, NoSuchMethodException {
        System.out.println("Не нашли, лезем в базу и загружаем в кэш одиночный объект");
        DataObject dataObject = loadingService.getDataObjectByIdAlternative(id);
        return dataObject;
    }

    public static Map<Integer,DataObject> getListDataObjectById(Iterable<? extends Integer> keys) throws InvocationTargetException, SQLException, IllegalAccessException, NoSuchMethodException {
        System.out.println("Не нашли, лезем в базу и загружаем в кэш список нужных объектов после обработки фильтров");

        Iterator<? extends Integer> iterator = keys.iterator();

        ArrayList<Integer> list = new ArrayList<>();

        while (iterator.hasNext()) {
            list.add(iterator.next());
        }

        System.out.println("Размер листа c ключами " + list.size());

        ArrayList<DataObject> dataObjectList = loadingService.getListDataObjectByListIdAlternative(list);

        System.out.println("Размер листа c объектами  " + dataObjectList.size());

        final Map<Integer, DataObject> map = new HashMap<>(dataObjectList.size());
        for (final DataObject dataObject : dataObjectList)
            map.put(dataObject.getId(), dataObject);

        return map;
    }

}
