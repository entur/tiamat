create table stop_place_children (stop_place_id bigint not null, children_id bigint not null, primary key (stop_place_id, children_id));
alter table stop_place_children add constraint UK_kj0a7ruk5k2bub2028nbkqwtw unique (children_id);
alter table stop_place_children add constraint FK4erbijiihg4wb6qblaolq3kwn foreign key (children_id) references stop_place(id);
alter table stop_place_children add constraint FKlljrfiip140imskbef529knwo foreign key (stop_place_id) references stop_place(id);