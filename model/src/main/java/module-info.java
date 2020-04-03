module be.hogent.cafe.model {
    requires java.sql;
    requires log4j.api;
    requires log4j.core;
    requires kernel;
    requires layout;
    requires jfreechart;
    exports be.hogent.cafe.model;
    exports be.hogent.cafe.model.dao;
}