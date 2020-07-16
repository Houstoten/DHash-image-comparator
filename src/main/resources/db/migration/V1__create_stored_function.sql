create or replace function match_percent(bigint, bigint) returns decimal as
$$
begin
    return (1 - LENGTH(
                        REPLACE
                            (CAST
                                 (cast(
                                        ($1 # $2) as bit(64))
                                 AS TEXT), '0', ''))::decimal / 64);
end
$$ language plpgsql;