--liquibase formatted sql

--changeset Hroniko:1 

-- Table: Attributes
CREATE TABLE Attributes (
    attr_id number(6,0)  NOT NULL,
    attr_name varchar2(20)  NOT NULL,
    CONSTRAINT attr_name UNIQUE (attr_name),
    CONSTRAINT Attributes_pk PRIMARY KEY (attr_id)
) ;
-- Table: Obj_attributes
CREATE TABLE Obj_attributes (
    object_type_id number(6,0)  NOT NULL,
    attr_id number(6,0)  NOT NULL
) ;
-- Table: Obj_types
CREATE TABLE Obj_types (
    object_type_id number(6,0)  NOT NULL,
    name varchar2(20)  NOT NULL,
    CONSTRAINT Obj_types_ak_1 UNIQUE (name),
    CONSTRAINT Obj_types_pk PRIMARY KEY (object_type_id)
) ;
-- Table: Objects
CREATE TABLE Objects (
    object_id number(6,0)  NOT NULL,
    object_type_id number(6,0)  NOT NULL,
    object_name varchar2(70),
    CONSTRAINT Objects_pk PRIMARY KEY (object_id)
) ;
-- Table: Params
CREATE TABLE Params (
    object_id number(6,0)  NOT NULL,
    attr_id number(6,0)  NOT NULL,
    value varchar2(70)
) ;
-- Table: References
CREATE TABLE References (
    object_id number(6,0)  NOT NULL,
    attr_id number(6,0)  NOT NULL,
    reference number(6,0)  NOT NULL
) ;
-- Table: Repository for BLOB files
CREATE TABLE Repository (
    object_id number(6,0)  NOT NULL,
    object_body BLOB -- сам файл в двоичном виде
) ;
-- foreign keys
-- Reference: Obj_attributes_Attributes (table: Obj_attributes)
ALTER TABLE Obj_attributes ADD CONSTRAINT Obj_attributes_Attributes
    FOREIGN KEY (attr_id)
    REFERENCES Attributes (attr_id);
-- Reference: Obj_attributes_Obj_types (table: Obj_attributes)
ALTER TABLE Obj_attributes ADD CONSTRAINT Obj_attributes_Obj_types
    FOREIGN KEY (object_type_id)
    REFERENCES Obj_types (object_type_id);
-- Reference: Obj_types_Objects (table: Objects)
ALTER TABLE Objects ADD CONSTRAINT Obj_types_Objects
    FOREIGN KEY (object_type_id)
    REFERENCES Obj_types (object_type_id);
-- Reference: Objects_References (table: References)
ALTER TABLE References ADD CONSTRAINT Objects_References
    FOREIGN KEY (reference)
    REFERENCES Objects (object_id);
-- Reference: Params_Attributes (table: Params)
ALTER TABLE Params ADD CONSTRAINT Params_Attributes
    FOREIGN KEY (attr_id)
    REFERENCES Attributes (attr_id);
-- Reference: Params_Objects (table: Params)
ALTER TABLE Params ADD CONSTRAINT Params_Objects
    FOREIGN KEY (object_id)
    REFERENCES Objects (object_id);
-- Reference: References_Attributes (table: References)
ALTER TABLE References ADD CONSTRAINT References_Attributes
    FOREIGN KEY (attr_id)
    REFERENCES Attributes (attr_id);
-- Reference: References_Objects (table: References)
ALTER TABLE References ADD CONSTRAINT References_Objects
    FOREIGN KEY (object_id)
    REFERENCES Objects (object_id);
-- Reference: Repository_Objects (table: Repository)
ALTER TABLE Repository ADD CONSTRAINT Repository_Objects
    FOREIGN KEY (object_id)
    REFERENCES Objects (object_id);
--rollback drop table Attributes cascade constraints;
--rollback drop table Obj_attributes cascade constraints;
--rollback drop table Obj_types cascade constraints;
--rollback drop table Objects cascade constraints;
--rollback drop table Params cascade constraints;
--rollback drop table References cascade constraints;
--rollback drop table Repository cascade constraints;

--changeset Hroniko:2
--Attributes(1-1000)

--User_Attributes (1-100)
INSERT INTO ATTRIBUTES (ATTR_ID, ATTR_NAME) VALUES ('1', 'name');
INSERT INTO ATTRIBUTES (ATTR_ID, ATTR_NAME) VALUES ('2', 'surname');
INSERT INTO ATTRIBUTES (ATTR_ID, ATTR_NAME) VALUES ('3', 'middle_name');
INSERT INTO ATTRIBUTES (ATTR_ID, ATTR_NAME) VALUES ('4', 'login');
INSERT INTO ATTRIBUTES (ATTR_ID, ATTR_NAME) VALUES ('5', 'birth_date');
INSERT INTO ATTRIBUTES (ATTR_ID, ATTR_NAME) VALUES ('6', 'e-mail');
INSERT INTO ATTRIBUTES (ATTR_ID, ATTR_NAME) VALUES ('7', 'password');
INSERT INTO ATTRIBUTES (ATTR_ID, ATTR_NAME) VALUES ('8', 'sex');
INSERT INTO ATTRIBUTES (ATTR_ID, ATTR_NAME) VALUES ('9', 'city');
INSERT INTO ATTRIBUTES (ATTR_ID, ATTR_NAME) VALUES ('10', 'additional_field');
INSERT INTO ATTRIBUTES (ATTR_ID, ATTR_NAME) VALUES ('11', 'picture');
INSERT INTO ATTRIBUTES (ATTR_ID, ATTR_NAME) VALUES ('12', 'friends');
INSERT INTO ATTRIBUTES (ATTR_ID, ATTR_NAME) VALUES ('13', 'task_id');
INSERT INTO ATTRIBUTES (ATTR_ID, ATTR_NAME) VALUES ('14', 'events');
INSERT INTO ATTRIBUTES (ATTR_ID, ATTR_NAME) VALUES ('15', 'confirmedEmail');
INSERT INTO ATTRIBUTES (ATTR_ID, ATTR_NAME) VALUES ('16', 'phone');
INSERT INTO ATTRIBUTES (ATTR_ID, ATTR_NAME) VALUES ('17', 'confirmedPhone');
INSERT INTO ATTRIBUTES (ATTR_ID, ATTR_NAME) VALUES ('18', 'calendar_file');
INSERT INTO ATTRIBUTES (ATTR_ID, ATTR_NAME) VALUES ('19', 'settings');

--Tasks_Attributes (101-200)
INSERT INTO ATTRIBUTES (ATTR_ID, ATTR_NAME) VALUES ('101', 'time_start');
INSERT INTO ATTRIBUTES (ATTR_ID, ATTR_NAME) VALUES ('102', 'time_end');
INSERT INTO ATTRIBUTES (ATTR_ID, ATTR_NAME) VALUES ('103', 'duration'); -- в часах
INSERT INTO ATTRIBUTES (ATTR_ID, ATTR_NAME) VALUES ('104', 'task_comment');
INSERT INTO ATTRIBUTES (ATTR_ID, ATTR_NAME) VALUES ('105', 'priority');
--Tasks_Attributes link to Creator User (host id) 2017-02-28
INSERT INTO ATTRIBUTES (ATTR_ID, ATTR_NAME) VALUES ('141', 'host_id');

--End Attributes


--Obj_Types(1001-2000)
INSERT INTO Obj_types (OBJECT_TYPE_ID, NAME) VALUES ('1001', 'User');
INSERT INTO Obj_types (OBJECT_TYPE_ID, NAME) VALUES ('1002', 'Tasks');
INSERT INTO OBJ_TYPES (OBJECT_TYPE_ID, NAME) VALUES ('1003', 'Message');
INSERT INTO OBJ_TYPES (OBJECT_TYPE_ID, NAME) VALUES ('1004', 'Meeting');
INSERT INTO Obj_types (OBJECT_TYPE_ID, NAME) VALUES ('1005', 'Calendar');
INSERT INTO Obj_types (OBJECT_TYPE_ID, NAME) VALUES ('1006', 'Settings');

--End Obj_Types


--Obj_Attributes

--User Object_Attributes
INSERT INTO Obj_Attributes (OBJECT_TYPE_ID, ATTR_ID) VALUES ('1001', '1');
INSERT INTO Obj_Attributes (OBJECT_TYPE_ID, ATTR_ID) VALUES ('1001', '2');
INSERT INTO Obj_Attributes (OBJECT_TYPE_ID, ATTR_ID) VALUES ('1001', '3');
INSERT INTO Obj_Attributes (OBJECT_TYPE_ID, ATTR_ID) VALUES ('1001', '4');
INSERT INTO Obj_Attributes (OBJECT_TYPE_ID, ATTR_ID) VALUES ('1001', '5');
INSERT INTO Obj_Attributes (OBJECT_TYPE_ID, ATTR_ID) VALUES ('1001', '6');
INSERT INTO Obj_Attributes (OBJECT_TYPE_ID, ATTR_ID) VALUES ('1001', '7');
INSERT INTO Obj_Attributes (OBJECT_TYPE_ID, ATTR_ID) VALUES ('1001', '8');
INSERT INTO Obj_Attributes (OBJECT_TYPE_ID, ATTR_ID) VALUES ('1001', '9');
INSERT INTO Obj_Attributes (OBJECT_TYPE_ID, ATTR_ID) VALUES ('1001', '10');
INSERT INTO Obj_Attributes (OBJECT_TYPE_ID, ATTR_ID) VALUES ('1001', '11');
INSERT INTO Obj_Attributes (OBJECT_TYPE_ID, ATTR_ID) VALUES ('1001', '12');
INSERT INTO Obj_Attributes (OBJECT_TYPE_ID, ATTR_ID) VALUES ('1001', '13');
INSERT INTO Obj_Attributes (OBJECT_TYPE_ID, ATTR_ID) VALUES ('1001', '14');
INSERT INTO Obj_Attributes (OBJECT_TYPE_ID, ATTR_ID) VALUES ('1001', '15');
INSERT INTO Obj_Attributes (OBJECT_TYPE_ID, ATTR_ID) VALUES ('1001', '16');
INSERT INTO Obj_Attributes (OBJECT_TYPE_ID, ATTR_ID) VALUES ('1001', '17');
INSERT INTO Obj_Attributes (OBJECT_TYPE_ID, ATTR_ID) VALUES ('1001', '18');
INSERT INTO Obj_Attributes (OBJECT_TYPE_ID, ATTR_ID) VALUES ('1001', '19');

--Tasks Object_Attributes
INSERT INTO Obj_Attributes (OBJECT_TYPE_ID, ATTR_ID) VALUES ('1002', '101');
INSERT INTO Obj_Attributes (OBJECT_TYPE_ID, ATTR_ID) VALUES ('1002', '102');
INSERT INTO Obj_Attributes (OBJECT_TYPE_ID, ATTR_ID) VALUES ('1002', '103');
INSERT INTO Obj_Attributes (OBJECT_TYPE_ID, ATTR_ID) VALUES ('1002', '104');
INSERT INTO Obj_Attributes (OBJECT_TYPE_ID, ATTR_ID) VALUES ('1002', '105');
--Tasks Object_Attributes link to Creator User (host id) 2017-02-28
INSERT INTO Obj_Attributes (OBJECT_TYPE_ID, ATTR_ID) VALUES ('1002', '141');

--End Obj_Attributes


--Objects(10001)

-- Tasks
--Task Id: 20001
INSERT INTO Objects (OBJECT_ID, OBJECT_TYPE_ID, OBJECT_NAME) VALUES ('20001', '1002', 'Task20001');
  INSERT INTO Params (OBJECT_ID, ATTR_ID, VALUE) VALUES ('20001', '101', '19.07.2017 17:30'); -- time_start
  INSERT INTO Params (OBJECT_ID, ATTR_ID, VALUE) VALUES ('20001', '102', '19.07.2017 21:50'); -- time_end
  INSERT INTO Params (OBJECT_ID, ATTR_ID, VALUE) VALUES ('20001', '103', '4,333'); -- duration
  INSERT INTO Params (OBJECT_ID, ATTR_ID, VALUE) VALUES ('20001', '104', 'Работа'); -- task_comment
  INSERT INTO Params (OBJECT_ID, ATTR_ID, VALUE) VALUES ('20001', '105', 'Style1'); -- priority
  
  --Task Id: 20002
INSERT INTO Objects (OBJECT_ID, OBJECT_TYPE_ID, OBJECT_NAME) VALUES ('20002', '1002', 'Task20002');
  INSERT INTO Params (OBJECT_ID, ATTR_ID, VALUE) VALUES ('20002', '101', '09.07.2017 08:30'); -- time_start
  INSERT INTO Params (OBJECT_ID, ATTR_ID, VALUE) VALUES ('20002', '102', '09.07.2017 17:30'); -- time_end
  INSERT INTO Params (OBJECT_ID, ATTR_ID, VALUE) VALUES ('20002', '103', '9'); -- duration
  INSERT INTO Params (OBJECT_ID, ATTR_ID, VALUE) VALUES ('20002', '104', 'Работа'); -- task_comment
  INSERT INTO Params (OBJECT_ID, ATTR_ID, VALUE) VALUES ('20002', '105', 'Style1'); -- priority
  
  --Task Id: 20003
INSERT INTO Objects (OBJECT_ID, OBJECT_TYPE_ID, OBJECT_NAME) VALUES ('20003', '1002', 'Task20003');
  INSERT INTO Params (OBJECT_ID, ATTR_ID, VALUE) VALUES ('20003', '101', '09.02.2017 20:00'); -- time_start
  INSERT INTO Params (OBJECT_ID, ATTR_ID, VALUE) VALUES ('20003', '102', '09.02.2017 22:30'); -- time_end
  INSERT INTO Params (OBJECT_ID, ATTR_ID, VALUE) VALUES ('20003', '103', '2,5'); -- duration
  INSERT INTO Params (OBJECT_ID, ATTR_ID, VALUE) VALUES ('20003', '104', 'Кинотеатр'); -- task_comment
  INSERT INTO Params (OBJECT_ID, ATTR_ID, VALUE) VALUES ('20003', '105', 'Style2'); -- priority
  
  --Task Id: 20004
INSERT INTO Objects (OBJECT_ID, OBJECT_TYPE_ID, OBJECT_NAME) VALUES ('20004', '1002', 'Task20004');
  INSERT INTO Params (OBJECT_ID, ATTR_ID, VALUE) VALUES ('20004', '101', '11.02.2017 17:45'); -- time_start
  INSERT INTO Params (OBJECT_ID, ATTR_ID, VALUE) VALUES ('20004', '102', '11.02.2017 19:40'); -- time_end
  INSERT INTO Params (OBJECT_ID, ATTR_ID, VALUE) VALUES ('20004', '103', '1'); -- duration
  INSERT INTO Params (OBJECT_ID, ATTR_ID, VALUE) VALUES ('20004', '104', 'Какие то дела'); -- task_comment
  INSERT INTO Params (OBJECT_ID, ATTR_ID, VALUE) VALUES ('20004', '105', 'Style3'); -- priority

-- Users
--User Id: 10001
INSERT INTO Objects (OBJECT_ID, OBJECT_TYPE_ID, OBJECT_NAME) VALUES ('10001', '1001', 'Геннадий Иванович Степанов');
  INSERT INTO Params (OBJECT_ID, ATTR_ID, VALUE) VALUES ('10001', '1', 'Геннадий'); --name
  INSERT INTO Params (OBJECT_ID, ATTR_ID, VALUE) VALUES ('10001', '2', 'Степанов'); --surname
  INSERT INTO Params (OBJECT_ID, ATTR_ID, VALUE) VALUES ('10001', '3', 'Иванович'); --middle_name
  INSERT INTO Params (OBJECT_ID, ATTR_ID, VALUE) VALUES ('10001', '4', 'gena322'); --login
  INSERT INTO Params (OBJECT_ID, ATTR_ID, VALUE) VALUES ('10001', '5', '09.07.1985 00:00'); --birthdate
  INSERT INTO Params (OBJECT_ID, ATTR_ID, VALUE) VALUES ('10001', '6', 'gena51rus@pochta.ru'); --e-mail
  INSERT INTO Params (OBJECT_ID, ATTR_ID, VALUE) VALUES ('10001', '7', '$2a$06$F2.VRxDdlpM1Uzp/IlZNlOHtjK3MNAO5vUuX7v8b0MmPiDvWVdbm2'); --password
  INSERT INTO Params (OBJECT_ID, ATTR_ID, VALUE) VALUES ('10001', '8', 'мужской'); --sex
  INSERT INTO Params (OBJECT_ID, ATTR_ID, VALUE) VALUES ('10001', '9', 'Воронеж'); --city
  INSERT INTO Params (OBJECT_ID, ATTR_ID, VALUE) VALUES ('10001', '10', 'Тут дополнительная информация'); --additional_field
  INSERT INTO Params (OBJECT_ID, ATTR_ID, VALUE) VALUES ('10001', '11', 'pic.jpg'); --picture
  INSERT INTO Params (OBJECT_ID, ATTR_ID, VALUE) VALUES ('10001', '12', ''); -- friends
  INSERT INTO Params (OBJECT_ID, ATTR_ID, VALUE) VALUES ('10001', '13', ''); --task_id
  INSERT INTO Params (OBJECT_ID, ATTR_ID, VALUE) VALUES ('10001', '15', 'true'); --confirmedEmail
INSERT INTO Params (OBJECT_ID, ATTR_ID, VALUE) VALUES ('10001', '16', '7**********'); --phone
INSERT INTO Params (OBJECT_ID, ATTR_ID, VALUE) VALUES ('10001', '17', 'true'); --confirmedPhone


    INSERT INTO References (OBJECT_ID, ATTR_ID, reference) VALUES ('10001', '13', '20001'); --task_id
    INSERT INTO References (OBJECT_ID, ATTR_ID, reference) VALUES ('10001', '13', '20004'); --task_id




	
--User Id: 10002
INSERT INTO Objects (OBJECT_ID, OBJECT_TYPE_ID, OBJECT_NAME) VALUES ('10002', '1001', 'Василий Сергеевич Рожненко');
  INSERT INTO Params (OBJECT_ID, ATTR_ID, VALUE) VALUES ('10002', '1', 'Василий'); --name
  INSERT INTO Params (OBJECT_ID, ATTR_ID, VALUE) VALUES ('10002', '2', 'Рожненко'); --surname
  INSERT INTO Params (OBJECT_ID, ATTR_ID, VALUE) VALUES ('10002', '3', 'Сергеевич'); --middle_name
  INSERT INTO Params (OBJECT_ID, ATTR_ID, VALUE) VALUES ('10002', '4', 'vasyan14'); --login
  INSERT INTO Params (OBJECT_ID, ATTR_ID, VALUE) VALUES ('10002', '5', '17.05.1995 00:00'); --birthdate
  INSERT INTO Params (OBJECT_ID, ATTR_ID, VALUE) VALUES ('10002', '6', 'vasyarozh@poshta.com'); --e-mail
  INSERT INTO Params (OBJECT_ID, ATTR_ID, VALUE) VALUES ('10002', '7', '$2a$06$.ZNeT3DLgl/t8ElWPd9xQOySO62NFK6Uw4uMTUJMgeWN0rJyHB8X.'); --password
  INSERT INTO Params (OBJECT_ID, ATTR_ID, VALUE) VALUES ('10002', '8', 'мужской'); --sex
  INSERT INTO Params (OBJECT_ID, ATTR_ID, VALUE) VALUES ('10002', '9', 'Москва'); --city
  INSERT INTO Params (OBJECT_ID, ATTR_ID, VALUE) VALUES ('10002', '10', 'Тут дополнительная информация о Васе'); --additional_field
  INSERT INTO Params (OBJECT_ID, ATTR_ID, VALUE) VALUES ('10002', '11', 'picvasya.jpg'); --picture
  INSERT INTO Params (OBJECT_ID, ATTR_ID, VALUE) VALUES ('10002', '12', ''); -- friends
  INSERT INTO References (OBJECT_ID, ATTR_ID, reference) VALUES ('10002', '12', '10001'); --friend
  INSERT INTO Params (OBJECT_ID, ATTR_ID, VALUE) VALUES ('10002', '13', ''); --task_id
  INSERT INTO Params (OBJECT_ID, ATTR_ID, VALUE) VALUES ('10001', '15', 'true'); --confirmed
INSERT INTO Params (OBJECT_ID, ATTR_ID, VALUE) VALUES ('10001', '16', '7**********'); --phone
INSERT INTO Params (OBJECT_ID, ATTR_ID, VALUE) VALUES ('10001', '17', 'true'); --confirmedPhone
  INSERT INTO References (OBJECT_ID, ATTR_ID, reference) VALUES ('10002', '13', '20002'); --task_id
  INSERT INTO References (OBJECT_ID, ATTR_ID, reference) VALUES ('10002', '13', '20003'); --task_id







--Message_Attributes (201-300) (Attributes of Message)
INSERT INTO ATTRIBUTES (ATTR_ID, ATTR_NAME) VALUES ('201', 'from_id');
INSERT INTO ATTRIBUTES (ATTR_ID, ATTR_NAME) VALUES ('202', 'to_id');
INSERT INTO ATTRIBUTES (ATTR_ID, ATTR_NAME) VALUES ('203', 'date_send');
INSERT INTO ATTRIBUTES (ATTR_ID, ATTR_NAME) VALUES ('204', 'read_status');
INSERT INTO ATTRIBUTES (ATTR_ID, ATTR_NAME) VALUES ('205', 'text');
INSERT INTO ATTRIBUTES (ATTR_ID, ATTR_NAME) VALUES ('206', 'from_name');
INSERT INTO ATTRIBUTES (ATTR_ID, ATTR_NAME) VALUES ('207', 'to_name');

--Message_Attributes (201-300) (add new Attribute of User)
INSERT INTO ATTRIBUTES (ATTR_ID, ATTR_NAME) VALUES ('30', 'Message_atr');


--Create new Object Message id 30001
INSERT INTO OBJECTS (OBJECT_ID, OBJECT_TYPE_ID, OBJECT_NAME) VALUES ('30001','1003','Message_30001');
--Create Attributes for Message id 30001 
INSERT INTO PARAMS (VALUE,OBJECT_ID,ATTR_ID) VALUES ('10001','30001','201');
INSERT INTO PARAMS (VALUE,OBJECT_ID,ATTR_ID) VALUES ('10002','30001','202');
INSERT INTO PARAMS (VALUE,OBJECT_ID,ATTR_ID) VALUES ('03.02.2017 12:31','30001','203');
INSERT INTO PARAMS (VALUE,OBJECT_ID,ATTR_ID) VALUES ('0','30001','204');
INSERT INTO PARAMS (VALUE,OBJECT_ID,ATTR_ID) VALUES ('Привет!Как_дела?','30001','205');
INSERT INTO PARAMS (VALUE,OBJECT_ID,ATTR_ID) VALUES ('Иван','30001','206');
INSERT INTO PARAMS (VALUE,OBJECT_ID,ATTR_ID) VALUES ('Петр','30001','207');
--Create new Reference between User and Message
INSERT INTO REFERENCES (OBJECT_ID, ATTR_ID, REFERENCE) VALUES ('10001', '30', '30001');


--Create new Object Message id 30002
INSERT INTO OBJECTS (OBJECT_ID, OBJECT_TYPE_ID, OBJECT_NAME) VALUES ('30002','1003','Message_30002');
--Create Attributes for Message id 30002
INSERT INTO PARAMS (VALUE,OBJECT_ID,ATTR_ID) VALUES ('10002','30002','201');
INSERT INTO PARAMS (VALUE,OBJECT_ID,ATTR_ID) VALUES ('10001','30002','202');
INSERT INTO PARAMS (VALUE,OBJECT_ID,ATTR_ID) VALUES ('03.02.2017 12:37','30002','203');
INSERT INTO PARAMS (VALUE,OBJECT_ID,ATTR_ID) VALUES ('0','30002','204');
INSERT INTO PARAMS (VALUE,OBJECT_ID,ATTR_ID) VALUES ('Нормально','30002','205');
INSERT INTO PARAMS (VALUE,OBJECT_ID,ATTR_ID) VALUES ('Петр','30002','206');
INSERT INTO PARAMS (VALUE,OBJECT_ID,ATTR_ID) VALUES ('Иван','30002','207');
--Create new Reference between User and Message
INSERT INTO REFERENCES (OBJECT_ID, ATTR_ID, REFERENCE) VALUES ('10002', '30', '30002');

INSERT INTO Obj_Attributes (OBJECT_TYPE_ID, ATTR_ID) VALUES ('1003', '201');
INSERT INTO Obj_Attributes (OBJECT_TYPE_ID, ATTR_ID) VALUES ('1003', '202');
INSERT INTO Obj_Attributes (OBJECT_TYPE_ID, ATTR_ID) VALUES ('1003', '203');
INSERT INTO Obj_Attributes (OBJECT_TYPE_ID, ATTR_ID) VALUES ('1003', '204');
INSERT INTO Obj_Attributes (OBJECT_TYPE_ID, ATTR_ID) VALUES ('1003', '205');
INSERT INTO Obj_Attributes (OBJECT_TYPE_ID, ATTR_ID) VALUES ('1003', '206');
INSERT INTO Obj_Attributes (OBJECT_TYPE_ID, ATTR_ID) VALUES ('1003', '207');

--Tasks_Attributes (301-400) (Attributes of Meeting)
INSERT INTO ATTRIBUTES (ATTR_ID, ATTR_NAME) VALUES ('301', 'title');
INSERT INTO ATTRIBUTES (ATTR_ID, ATTR_NAME) VALUES ('302', 'date_start');
INSERT INTO ATTRIBUTES (ATTR_ID, ATTR_NAME) VALUES ('303', 'date_end');
INSERT INTO ATTRIBUTES (ATTR_ID, ATTR_NAME) VALUES ('304', 'info');
INSERT INTO ATTRIBUTES (ATTR_ID, ATTR_NAME) VALUES ('305', 'organizer');
INSERT INTO ATTRIBUTES (ATTR_ID, ATTR_NAME) VALUES ('306', 'tag');
INSERT INTO ATTRIBUTES (ATTR_ID, ATTR_NAME) VALUES ('307', 'member');
INSERT INTO ATTRIBUTES (ATTR_ID, ATTR_NAME) VALUES ('308', 'meetingEvents');

--Meeting Object_Attributes
INSERT INTO Obj_Attributes (OBJECT_TYPE_ID, ATTR_ID) VALUES ('1004', '301');
INSERT INTO Obj_Attributes (OBJECT_TYPE_ID, ATTR_ID) VALUES ('1004', '302');
INSERT INTO Obj_Attributes (OBJECT_TYPE_ID, ATTR_ID) VALUES ('1004', '303');
INSERT INTO Obj_Attributes (OBJECT_TYPE_ID, ATTR_ID) VALUES ('1004', '304');
INSERT INTO Obj_Attributes (OBJECT_TYPE_ID, ATTR_ID) VALUES ('1004', '305');
INSERT INTO Obj_Attributes (OBJECT_TYPE_ID, ATTR_ID) VALUES ('1004', '306');
INSERT INTO Obj_Attributes (OBJECT_TYPE_ID, ATTR_ID) VALUES ('1004', '307');
INSERT INTO Obj_Attributes (OBJECT_TYPE_ID, ATTR_ID) VALUES ('1004', '308');

-- BLOB Files
--File Id: 50001
INSERT INTO Objects (OBJECT_ID, OBJECT_TYPE_ID, OBJECT_NAME) VALUES ('50001', '1005', 'file.dat');
INSERT INTO References (OBJECT_ID, ATTR_ID, reference) VALUES ('10001', '18', '50001'); -- file google
INSERT INTO Repository (OBJECT_ID, OBJECT_BODY) VALUES ('50001', null);


--AdvancedSettings_Attributes (401-500)
INSERT INTO ATTRIBUTES (ATTR_ID, ATTR_NAME) VALUES ('401', 'user_id');
INSERT INTO ATTRIBUTES (ATTR_ID, ATTR_NAME) VALUES ('402', 'emailNewMessage');
INSERT INTO ATTRIBUTES (ATTR_ID, ATTR_NAME) VALUES ('403', 'emailNewFriend');
INSERT INTO ATTRIBUTES (ATTR_ID, ATTR_NAME) VALUES ('404', 'emailMeetingInvite');
INSERT INTO ATTRIBUTES (ATTR_ID, ATTR_NAME) VALUES ('405', 'phoneNewMessage');
INSERT INTO ATTRIBUTES (ATTR_ID, ATTR_NAME) VALUES ('406', 'phoneNewFriend');
INSERT INTO ATTRIBUTES (ATTR_ID, ATTR_NAME) VALUES ('407', 'phoneMeetingInvite');

--Create new AdvancedSettings 
INSERT INTO OBJECTS (OBJECT_ID, OBJECT_TYPE_ID, OBJECT_NAME) VALUES ('40001','1006','Settings_User_10001');
INSERT INTO OBJECTS (OBJECT_ID, OBJECT_TYPE_ID, OBJECT_NAME) VALUES ('40002','1006','Settings_User_10002');

--Settings User10001
INSERT INTO Params (OBJECT_ID, ATTR_ID, VALUE) VALUES ('40001', '401', '10001');
INSERT INTO Params (OBJECT_ID, ATTR_ID, VALUE) VALUES ('40001', '402', 'true');
INSERT INTO Params (OBJECT_ID, ATTR_ID, VALUE) VALUES ('40001', '403', 'true'); 
INSERT INTO Params (OBJECT_ID, ATTR_ID, VALUE) VALUES ('40001', '404', 'true'); 
INSERT INTO Params (OBJECT_ID, ATTR_ID, VALUE) VALUES ('40001', '405', 'true'); 
INSERT INTO Params (OBJECT_ID, ATTR_ID, VALUE) VALUES ('40001', '406', 'true'); 
INSERT INTO Params (OBJECT_ID, ATTR_ID, VALUE) VALUES ('40001', '407', 'true');

INSERT INTO References (OBJECT_ID, ATTR_ID, reference) VALUES ('10001', '19', '40001'); --settings

--Settings User10002
INSERT INTO Params (OBJECT_ID, ATTR_ID, VALUE) VALUES ('40002', '401', '10002');
INSERT INTO Params (OBJECT_ID, ATTR_ID, VALUE) VALUES ('40002', '402', 'true');
INSERT INTO Params (OBJECT_ID, ATTR_ID, VALUE) VALUES ('40002', '403', 'true'); 
INSERT INTO Params (OBJECT_ID, ATTR_ID, VALUE) VALUES ('40002', '404', 'true'); 
INSERT INTO Params (OBJECT_ID, ATTR_ID, VALUE) VALUES ('40002', '405', 'true'); 
INSERT INTO Params (OBJECT_ID, ATTR_ID, VALUE) VALUES ('40002', '406', 'true'); 
INSERT INTO Params (OBJECT_ID, ATTR_ID, VALUE) VALUES ('40002', '407', 'true'); 


INSERT INTO References (OBJECT_ID, ATTR_ID, reference) VALUES ('10002', '19', '40002'); --settings