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
--rollback drop table Attributes cascade constraints;
--rollback drop table Obj_attributes cascade constraints;
--rollback drop table Obj_types cascade constraints;
--rollback drop table Objects cascade constraints;
--rollback drop table Params cascade constraints;
--rollback drop table References cascade constraints;

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
INSERT INTO ATTRIBUTES (ATTR_ID, ATTR_NAME) VALUES ('9', 'country');
INSERT INTO ATTRIBUTES (ATTR_ID, ATTR_NAME) VALUES ('10', 'additional_field');
INSERT INTO ATTRIBUTES (ATTR_ID, ATTR_NAME) VALUES ('11', 'picture');
INSERT INTO ATTRIBUTES (ATTR_ID, ATTR_NAME) VALUES ('12', 'friends');
INSERT INTO ATTRIBUTES (ATTR_ID, ATTR_NAME) VALUES ('13', 'task_id');
INSERT INTO ATTRIBUTES (ATTR_ID, ATTR_NAME) VALUES ('14', 'events');

--Tasks_Attributes (101-200)
INSERT INTO ATTRIBUTES (ATTR_ID, ATTR_NAME) VALUES ('101', 'time_start');
INSERT INTO ATTRIBUTES (ATTR_ID, ATTR_NAME) VALUES ('102', 'time_end');
INSERT INTO ATTRIBUTES (ATTR_ID, ATTR_NAME) VALUES ('103', 'duration'); -- в часах
INSERT INTO ATTRIBUTES (ATTR_ID, ATTR_NAME) VALUES ('104', 'task_comment');
INSERT INTO ATTRIBUTES (ATTR_ID, ATTR_NAME) VALUES ('105', 'priority');

--End Attributes


--Obj_Types(1001-2000)

INSERT INTO Obj_types (OBJECT_TYPE_ID, NAME) VALUES ('1001', 'User');
INSERT INTO Obj_types (OBJECT_TYPE_ID, NAME) VALUES ('1002', 'Tasks');

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

--Tasks Object_Attributes
INSERT INTO Obj_Attributes (OBJECT_TYPE_ID, ATTR_ID) VALUES ('1002', '101');
INSERT INTO Obj_Attributes (OBJECT_TYPE_ID, ATTR_ID) VALUES ('1002', '102');
INSERT INTO Obj_Attributes (OBJECT_TYPE_ID, ATTR_ID) VALUES ('1002', '103');
INSERT INTO Obj_Attributes (OBJECT_TYPE_ID, ATTR_ID) VALUES ('1002', '104');
INSERT INTO Obj_Attributes (OBJECT_TYPE_ID, ATTR_ID) VALUES ('1002', '105');

--End Obj_Attributes


--Objects(10001)

-- Tasks
--Task Id: 20001
INSERT INTO Objects (OBJECT_ID, OBJECT_TYPE_ID, OBJECT_NAME) VALUES ('20001', '1002', 'Task20001');
  INSERT INTO Params (OBJECT_ID, ATTR_ID, VALUE) VALUES ('20001', '101', '17:30'); -- time_start
  INSERT INTO Params (OBJECT_ID, ATTR_ID, VALUE) VALUES ('20001', '102', '21:50'); -- time_end
  INSERT INTO Params (OBJECT_ID, ATTR_ID, VALUE) VALUES ('20001', '103', '4,333'); -- duration
  INSERT INTO Params (OBJECT_ID, ATTR_ID, VALUE) VALUES ('20001', '104', 'Работа'); -- task_comment
  INSERT INTO Params (OBJECT_ID, ATTR_ID, VALUE) VALUES ('20001', '105', '1'); -- priority
  
  --Task Id: 20002
INSERT INTO Objects (OBJECT_ID, OBJECT_TYPE_ID, OBJECT_NAME) VALUES ('20002', '1002', 'Task20002');
  INSERT INTO Params (OBJECT_ID, ATTR_ID, VALUE) VALUES ('20002', '101', '08:30'); -- time_start
  INSERT INTO Params (OBJECT_ID, ATTR_ID, VALUE) VALUES ('20002', '102', '17:30'); -- time_end
  INSERT INTO Params (OBJECT_ID, ATTR_ID, VALUE) VALUES ('20002', '103', '9'); -- duration
  INSERT INTO Params (OBJECT_ID, ATTR_ID, VALUE) VALUES ('20002', '104', 'Работа'); -- task_comment
  INSERT INTO Params (OBJECT_ID, ATTR_ID, VALUE) VALUES ('20002', '105', '1'); -- priority
  
  --Task Id: 20003
INSERT INTO Objects (OBJECT_ID, OBJECT_TYPE_ID, OBJECT_NAME) VALUES ('20003', '1002', 'Task20003');
  INSERT INTO Params (OBJECT_ID, ATTR_ID, VALUE) VALUES ('20003', '101', '20:00'); -- time_start
  INSERT INTO Params (OBJECT_ID, ATTR_ID, VALUE) VALUES ('20003', '102', '22:30'); -- time_end
  INSERT INTO Params (OBJECT_ID, ATTR_ID, VALUE) VALUES ('20003', '103', '2,5'); -- duration
  INSERT INTO Params (OBJECT_ID, ATTR_ID, VALUE) VALUES ('20003', '104', 'Кинотеатр'); -- task_comment
  INSERT INTO Params (OBJECT_ID, ATTR_ID, VALUE) VALUES ('20003', '105', '2'); -- priority
  
  --Task Id: 20004
INSERT INTO Objects (OBJECT_ID, OBJECT_TYPE_ID, OBJECT_NAME) VALUES ('20004', '1002', 'Task20004');
  INSERT INTO Params (OBJECT_ID, ATTR_ID, VALUE) VALUES ('20004', '101', '17:45'); -- time_start
  INSERT INTO Params (OBJECT_ID, ATTR_ID, VALUE) VALUES ('20004', '102', '19:40'); -- time_end
  INSERT INTO Params (OBJECT_ID, ATTR_ID, VALUE) VALUES ('20004', '103', '1'); -- duration
  INSERT INTO Params (OBJECT_ID, ATTR_ID, VALUE) VALUES ('20004', '104', 'Какие то дела'); -- task_comment
  INSERT INTO Params (OBJECT_ID, ATTR_ID, VALUE) VALUES ('20004', '105', '4'); -- priority

-- Users
--User Id: 10001
INSERT INTO Objects (OBJECT_ID, OBJECT_TYPE_ID, OBJECT_NAME) VALUES ('10001', '1001', 'Геннадий �?ванович Степанов');
  INSERT INTO Params (OBJECT_ID, ATTR_ID, VALUE) VALUES ('10001', '1', 'Геннадий'); --name
  INSERT INTO Params (OBJECT_ID, ATTR_ID, VALUE) VALUES ('10001', '2', '�?ванович'); --surname
  INSERT INTO Params (OBJECT_ID, ATTR_ID, VALUE) VALUES ('10001', '3', 'Степанов'); --middle_name
  INSERT INTO Params (OBJECT_ID, ATTR_ID, VALUE) VALUES ('10001', '4', 'gena322'); --login
  INSERT INTO Params (OBJECT_ID, ATTR_ID, VALUE) VALUES ('10001', '5', '09.07.1985'); --birthdate
  INSERT INTO Params (OBJECT_ID, ATTR_ID, VALUE) VALUES ('10001', '6', 'gena51rus@pochta.ru'); --e-mail
  INSERT INTO Params (OBJECT_ID, ATTR_ID, VALUE) VALUES ('10001', '7', 'password123'); --password
  INSERT INTO Params (OBJECT_ID, ATTR_ID, VALUE) VALUES ('10001', '8', 'мужской'); --sex
  INSERT INTO Params (OBJECT_ID, ATTR_ID, VALUE) VALUES ('10001', '9', 'Россия'); --country
  INSERT INTO Params (OBJECT_ID, ATTR_ID, VALUE) VALUES ('10001', '10', 'Тут дополнительная информация'); --additional_field
  INSERT INTO Params (OBJECT_ID, ATTR_ID, VALUE) VALUES ('10001', '11', 'pic.jpg'); --picture
  INSERT INTO Params (OBJECT_ID, ATTR_ID, VALUE) VALUES ('10001', '12', ''); -- friends
  INSERT INTO Params (OBJECT_ID, ATTR_ID, VALUE) VALUES ('10001', '13', ''); --task_id
    INSERT INTO References (OBJECT_ID, ATTR_ID, reference) VALUES ('10001', '13', '20001'); --task_id
    INSERT INTO References (OBJECT_ID, ATTR_ID, reference) VALUES ('10001', '13', '20004'); --task_id
--User Id: 10002
INSERT INTO Objects (OBJECT_ID, OBJECT_TYPE_ID, OBJECT_NAME) VALUES ('10002', '1001', 'Василий Сергеевич Рожненко');
  INSERT INTO Params (OBJECT_ID, ATTR_ID, VALUE) VALUES ('10002', '1', 'Василий'); --name
  INSERT INTO Params (OBJECT_ID, ATTR_ID, VALUE) VALUES ('10002', '2', 'Сергеевич'); --surname
  INSERT INTO Params (OBJECT_ID, ATTR_ID, VALUE) VALUES ('10002', '3', 'Рожненко'); --middle_name
  INSERT INTO Params (OBJECT_ID, ATTR_ID, VALUE) VALUES ('10002', '4', 'vasyan14'); --login
  INSERT INTO Params (OBJECT_ID, ATTR_ID, VALUE) VALUES ('10002', '5', '17.05.1995'); --birthdate
  INSERT INTO Params (OBJECT_ID, ATTR_ID, VALUE) VALUES ('10002', '6', 'vasyarozh@poshta.com'); --e-mail
  INSERT INTO Params (OBJECT_ID, ATTR_ID, VALUE) VALUES ('10002', '7', 'passwordvasyana'); --password
  INSERT INTO Params (OBJECT_ID, ATTR_ID, VALUE) VALUES ('10002', '8', 'мужской'); --sex
  INSERT INTO Params (OBJECT_ID, ATTR_ID, VALUE) VALUES ('10002', '9', 'Россия'); --country
  INSERT INTO Params (OBJECT_ID, ATTR_ID, VALUE) VALUES ('10002', '10', 'Тут дополнительная информация о Васе'); --additional_field
  INSERT INTO Params (OBJECT_ID, ATTR_ID, VALUE) VALUES ('10002', '11', 'picvasya.jpg'); --picture
  INSERT INTO Params (OBJECT_ID, ATTR_ID, VALUE) VALUES ('10002', '12', ''); -- friends
  INSERT INTO References (OBJECT_ID, ATTR_ID, reference) VALUES ('10002', '12', '10001'); --friend
  INSERT INTO Params (OBJECT_ID, ATTR_ID, VALUE) VALUES ('10002', '13', ''); --task_id
  INSERT INTO References (OBJECT_ID, ATTR_ID, reference) VALUES ('10002', '13', '20002'); --task_id
  INSERT INTO References (OBJECT_ID, ATTR_ID, reference) VALUES ('10002', '13', '20003'); --task_id