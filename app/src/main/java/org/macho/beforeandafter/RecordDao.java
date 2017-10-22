package org.macho.beforeandafter;

import org.macho.beforeandafter.record.Record;

import java.util.ArrayList;
import java.util.List;

import io.realm.Realm;
import io.realm.RealmResults;

/**
 * Created by yuukimatsushima on 2017/08/15.
 */

public class RecordDao {
    private static RecordDao recordDao;
    public static RecordDao getInstance() {
        if (recordDao == null) {
            recordDao = new RecordDao();
        }
        return recordDao;
    }
    public List<Record> findAll() {
        List<Record> records = new ArrayList<>();
        try (Realm realm = Realm.getDefaultInstance()) {
            RealmResults<RecordDto> results = realm.where(RecordDto.class).findAll().sort("date");
            List<RecordDto> dtos = results.subList(0, results.size());
            for (RecordDto dto : dtos) {
                records.add(new Record(dto.getDate(), dto.getWeight(), dto.getRate(), dto.getFrontImagePath(), dto.getSideImagePath(), dto.getMemo()));
            }
        }
        return records;
    }

    public Record find(long date) {
        Record record = null;
        try (Realm realm = Realm.getDefaultInstance()) {
            RealmResults<RecordDto> realmResults = realm.where(RecordDto.class).equalTo("date", date).findAll();
            RecordDto dto = realmResults.get(0);
            if (dto != null) {
                record = new Record(dto.getDate(), dto.getWeight(), dto.getRate(), dto.getFrontImagePath(), dto.getSideImagePath(), dto.getMemo());
            }
        }
        return record;
    }
    public List<Record> find(long from, long to) {
        List<Record> records = new ArrayList<>();
        try (Realm realm = Realm.getDefaultInstance()) {
            RealmResults<RecordDto> results = realm.where(RecordDto.class).between("date", from, to).findAll().sort("date");
            List<RecordDto> dtos = results.subList(0, results.size());
            for(RecordDto dto : dtos) {
                records.add(new Record(dto.getDate(), dto.getWeight(), dto.getRate(), dto.getFrontImagePath(), dto.getSideImagePath(), dto.getMemo()));
            }
            return records;
        }
    }

//    public List<Work> find(String workName, long from, long to) {
//        List<Work> works = new ArrayList<>();
//        try (Realm realm = Realm.getDefaultInstance()) {
//            RealmResults<RecordDto> tasks = null;
//            if (workName == null) {
//                tasks = realm.where(RecordDto.class).between("from", from, to).findAll();
//            } else {
//                tasks = realm.where(RecordDto.class).between("from", from, to).equalTo("project", workName).findAll();
//            }
//            List<RecordDto> taskss = tasks.subList(0, tasks.size());
//            for (RecordDto task : taskss) {
//                works.add(new Work(task.getFrom(), task.getElapsedMilliSeconds(), task.getProject(), task.getDescription(), task.getAmount()));
//            }
//        }
//        return works;
//    }
    public void register(final Record record) {
        try (Realm realm = Realm.getDefaultInstance()) {
            realm.executeTransaction(new Realm.Transaction() {
                @Override
                public void execute(Realm realm) {
                    RecordDto registered = realm.createObject(RecordDto.class, record.getDate());
                    registered.setWeight(record.getWeight());
                    registered.setRate(record.getRate());
                    registered.setFrontImagePath(record.getFrontImagePath());
                    registered.setSideImagePath(record.getSideImagePath());
                    registered.setMemo(record.getMemo());
                }
            });
        }
    }

    public void update(final Record record) {
        try (Realm realm = Realm.getDefaultInstance()) {
            realm.executeTransaction(new Realm.Transaction() {
                @Override
                public void execute(Realm realm) {
//                    realm.insertOrUpdate(new RecordDto(record.getDate(), record.getWeight(), record.getRate(), record.getFrontImagePath(), record.getSideImagePath()));
                    realm.copyToRealmOrUpdate(new RecordDto(record.getDate(), record.getWeight(), record.getRate(), record.getFrontImagePath(), record.getSideImagePath(), record.getMemo()));
                }
            });
        }
    }

    public void delete(final long date) {
        try (Realm realm = Realm.getDefaultInstance()) {
            realm.executeTransaction(new Realm.Transaction() {
                @Override
                public void execute(Realm realm) {
                    RecordDto dto = realm.where(RecordDto.class).equalTo("date", date).findFirst();
                    dto.deleteFromRealm();
                }
            });
        }
    }

    public void deleteAll() {
        try (Realm realm = Realm.getDefaultInstance()) {
            realm.executeTransaction(new Realm.Transaction() {
                @Override
                public void execute(Realm realm) {
                    RealmResults<RecordDto> results = realm.where(RecordDto.class).findAll();
                    for (RecordDto dto : results){
                        dto.deleteFromRealm();
                    }
                }
            });
        }
    }


}
