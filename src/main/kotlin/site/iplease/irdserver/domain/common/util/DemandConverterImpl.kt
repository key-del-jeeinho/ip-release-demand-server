package site.iplease.irdserver.domain.common.util

import org.springframework.stereotype.Component
import reactor.core.publisher.Mono
import reactor.kotlin.core.publisher.toMono
import site.iplease.irdserver.domain.common.data.entity.Demand
import site.iplease.irdserver.domain.common.data.request.CreateReleaseDemandRequest
import site.iplease.irdserver.domain.common.data.response.CreateReleaseDemandResponse
import site.iplease.irdserver.domain.common.dto.DemandDto

@Component
class DemandConverterImpl: DemandConverter {
    override fun toEntity(dto: DemandDto): Mono<Demand> =
        Unit.toMono().map { Demand(
            id = dto.id,
            assignIpId = dto.assignIpId,
            issuerId = dto.issuerId
        ) }

    override fun toDto(entity: Demand): Mono<DemandDto> =
        Unit.toMono().map { DemandDto(
            id = entity.id,
            assignIpId = entity.assignIpId,
            issuerId = entity.issuerId
        ) }

    override fun toDto(request: CreateReleaseDemandRequest): Mono<DemandDto> =
        Unit.toMono().map { DemandDto(
            id = 0,
            assignIpId = request.assignIpId,
            issuerId = request.issuerId
        ) }

    override fun toResponse(resultDto: DemandDto): Mono<CreateReleaseDemandResponse> =
        Unit.toMono().map { CreateReleaseDemandResponse(demandId = resultDto.id) }
}