package dutchiepay.backend.domain.notice.exception;

import lombok.Getter;

@Getter
public class NoticeException extends RuntimeException {

    private final NoticeErrorCode noticeErrorCode;

    public NoticeException(NoticeErrorCode noticeErrorCode) {
      super(noticeErrorCode.getMessage());
      this.noticeErrorCode = noticeErrorCode;
    }

    @Override
    public String toString() {
        return String.format("NoticeErrorException(code=%s, message=%s)",
                noticeErrorCode.name(), noticeErrorCode.getMessage());
    }
}
