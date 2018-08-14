package sknictik.wafercodingchallenge.domain;

import java.util.List;

import sknictik.wafercodingchallenge.domain.model.Info;

public interface IInfoCommand {

    List<Info> loadInfoList() throws Exception;

}
