package com.citel.statistics.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.citel.statistics.dto.query.QueryRange;
import com.citel.statistics.dto.vo.PersonVO;
import com.citel.statistics.entity.Person;
import org.apache.ibatis.annotations.Param;

import java.util.List;
import java.util.Map;

/**
 * 人员信息 Mapper
 */
public interface PersonMapper extends BaseMapper<Person> {

    /**
     * 按多区间查询分页列表
     *
     * @param mode    查询模式：age-年龄，mileage-里程，time-时间
     * @param ranges  区间列表
     * @param refYear 年龄计算参考年
     * @param offset  偏移量
     * @param size    每页条数
     * @return 人员列表
     */
    List<PersonVO> selectPageByRanges(@Param("mode") String mode,
                                      @Param("ranges") List<QueryRange> ranges,
                                      @Param("refYear") int refYear,
                                      @Param("offset") int offset,
                                      @Param("size") int size);

    /**
     * 按多区间统计总数
     *
     * @param mode    查询模式
     * @param ranges  区间列表
     * @param refYear 年龄计算参考年
     * @return 记录总数
     */
    long selectCountByRanges(@Param("mode") String mode,
                             @Param("ranges") List<QueryRange> ranges,
                             @Param("refYear") int refYear);

    /**
     * 逐区间统计记录数（单行返回，列名为 c_0、c_1...）
     *
     * @param mode    查询模式
     * @param ranges  区间列表
     * @param refYear 年龄计算参考年
     * @return 统计结果 Map
     */
    Map<String, Object> countByRanges(@Param("mode") String mode,
                                      @Param("ranges") List<QueryRange> ranges,
                                      @Param("refYear") int refYear);
}
