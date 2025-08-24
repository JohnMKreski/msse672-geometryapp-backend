create table quads
(
    id    int auto_increment
        primary key,
    sideA double not null,
    sideB double not null,
    sideC double not null,
    sideD double not null,
    type VARCHAR(50) NOT NULL
);