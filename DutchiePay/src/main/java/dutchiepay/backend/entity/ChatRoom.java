package dutchiepay.backend.entity;

import dutchiepay.backend.global.config.Auditing;
import jakarta.persistence.*;
import lombok.*;

@Entity
@Builder
@Table(name = "Chatroom")
@Getter
@AllArgsConstructor
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class ChatRoom extends Auditing {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long chatroomId;

    @Column(nullable = false)
    private String chatRoomName;

    private String chatRoomImg;

    @Column(nullable = false)
    private String type;

    @Column(nullable = false)
    private Long postId;

    @Column(nullable = false)
    private Integer maxPartInc;

    private Integer nowPartInc;

    public void joinUser() {
        this.nowPartInc++;
    }

    public void leave() {
        this.nowPartInc--;
    }
}
