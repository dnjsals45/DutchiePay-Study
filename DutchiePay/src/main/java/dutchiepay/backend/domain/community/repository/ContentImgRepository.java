package dutchiepay.backend.domain.community.repository;

import dutchiepay.backend.entity.ContentImg;
import dutchiepay.backend.entity.Free;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ContentImgRepository extends JpaRepository<ContentImg, Long> {
    List<ContentImg> findContentImgByFree(Free free);
}
