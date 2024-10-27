package kr.co.conceptbe.notification_setting.exception;

import static org.springframework.http.HttpStatus.BAD_REQUEST;

import kr.co.conceptbe.common.exception.ConceptBeException;
import kr.co.conceptbe.common.exception.ErrorCode;

public class InvalidBranchException extends ConceptBeException {

  public InvalidBranchException() {
    super(new ErrorCode(BAD_REQUEST, "1개 이상 10개 이하 선택해야합니다."));
  }

}
