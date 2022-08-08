package site.iplease.irdserver.domain.reserve.util

import com.nhaarman.mockitokotlin2.mock
import com.nhaarman.mockitokotlin2.times
import com.nhaarman.mockitokotlin2.verify
import com.nhaarman.mockitokotlin2.whenever
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test
import reactor.kotlin.core.publisher.toMono
import site.iplease.irdserver.domain.reserve.data.type.ReservePermission
import site.iplease.irdserver.infra.account.data.dto.ProfileDto
import site.iplease.irdserver.infra.account.data.type.PermissionType
import site.iplease.irdserver.infra.account.service.AccountQueryService
import site.iplease.irdserver.infra.assign_ip.data.dto.AssignIpDto
import site.iplease.irdserver.infra.assign_ip.service.AssignIpQueryService
import kotlin.random.Random

class ReservePermissionValidatorImplTest {
    private lateinit var accountQueryService: AccountQueryService
    private lateinit var assignIpQueryService: AssignIpQueryService
    private lateinit var target: ReservePermissionValidatorImpl

    @BeforeEach
    fun beforeEach() {
        accountQueryService = mock()
        assignIpQueryService = mock()
        target = ReservePermissionValidatorImpl(accountQueryService, assignIpQueryService)
    }

    //프로필 DTO 로 가져온 정보로 권한을 검사한다.
    //예약추가를 위해서는 할당IP의 소유자이거나, 권한이 ADMINISTRATOR 이상이어야한다.
    @Test @DisplayName("IP할당해제예약추가권한검증 성공 테스트 - 최고관리자일 경우")
    fun testValidateCreate_success_administrator() {
        //given
        val issuerId = Random.nextLong()
        val assigneeId = Random.nextLong().let { var id = it; while(issuerId == id) id = Random.nextLong(); return@let id; }
        val assignIpId = Random.nextLong()
        val profile = mock<ProfileDto>()
        val assignIp = mock<AssignIpDto>()

        //when
        whenever(profile.permission).thenReturn(PermissionType.ADMINISTRATOR)
        whenever(assignIp.assigneeId).thenReturn(assigneeId)
        whenever(accountQueryService.findById(issuerId)).thenReturn(profile.toMono())
        whenever(assignIpQueryService.findById(assignIpId)).thenReturn(assignIp.toMono())

        val result = target.validate(ReservePermission.CREATE, issuerId = issuerId, assignIpId = assignIpId).block()!!

        //then
        assertEquals(result, Unit)
        verify(accountQueryService, times(1)).findById(issuerId)
    }

    @Test @DisplayName("IP할당해제예약추가권한검증 성공 테스트 - 할당IP소유자일 경우")
    fun testValidateCreate_success_owner() {
        //given
        val issuerId = Random.nextLong()
        val permission = PermissionType.values().filter { it != PermissionType.ADMINISTRATOR }.random()
        val assignIpId = Random.nextLong()
        val profile = mock<ProfileDto>()
        val assignIp = mock<AssignIpDto>()

        //when
        whenever(profile.permission).thenReturn(permission)
        whenever(assignIp.assigneeId).thenReturn(issuerId)
        whenever(accountQueryService.findById(issuerId)).thenReturn(profile.toMono())
        whenever(assignIpQueryService.findById(assignIpId)).thenReturn(assignIp.toMono())

        val result = target.validate(ReservePermission.CREATE, issuerId = issuerId, assignIpId = assignIpId).block()!!

        //then
        assertEquals(result, Unit)
        verify(assignIpQueryService, times(1)).findById(assignIpId)
    }
}