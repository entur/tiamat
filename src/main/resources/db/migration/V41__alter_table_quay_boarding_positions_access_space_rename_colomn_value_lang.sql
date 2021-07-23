alter table public.quay add COLUMN value to label_value;
alter table public.quay add COLUMN lang to label_lang;

alter table public.boarding_position rename COLUMN value to label_value;
alter table public.boarding_position rename COLUMN lang to label_lang;

alter table public.access_space rename COLUMN value to label_value
alter table public.access_space rename COLUMN lang to label_lang;