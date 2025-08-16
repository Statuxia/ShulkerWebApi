package me.statuxia.shulkerapi.response;

public class MeGameAccountResponse {

    private Long id;
    private String name;

    public Long getId() {
        return id;
    }

    public MeGameAccountResponse setId(Long id) {
        this.id = id;
        return this;
    }

    public String getName() {
        return name;
    }

    public MeGameAccountResponse setName(String name) {
        this.name = name;
        return this;
    }
}
