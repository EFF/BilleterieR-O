package ca.ulaval.glo4003;


public interface Bootstrapper {

    void initData() throws Exception;

    void deleteAll();
}
