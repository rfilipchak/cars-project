package com.playtika.carshop.dealservice;

import com.playtika.carshop.carshopservice.CarServiceImpl;
import com.playtika.carshop.converter.Converter;
import com.playtika.carshop.dao.DealsDao;
import com.playtika.carshop.dao.entity.CarShopEntity;
import com.playtika.carshop.dao.entity.DealEntity;
import com.playtika.carshop.dao.entity.PersonEntity;
import com.playtika.carshopcommon.dealstatus.DealStatus;
import com.playtika.carshopcommon.domain.CarSaleInfo;
import com.playtika.carshopcommon.domain.Deal;
import com.playtika.supportvalues.SupportTestValues;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Matchers.anyList;
import static org.mockito.Mockito.*;

@RunWith(MockitoJUnitRunner.class)
public class DealServiceTest extends SupportTestValues {

    private DealService dealService;
    @Mock
    private CarServiceImpl carService;
    @Mock
    private DealsDao dealsDao;
    @Mock
    private Converter converter;


    @Before
    public void init() {
        dealService = new DealServiceImp(carService, converter, dealsDao);
    }

    @Test
    public void shouldReturnDealEntityWhenAddDeal() {
        long id = 1L;
        CarSaleInfo carSaleInfo = generateCarSaleInfo(id, "AA-1");
        CarShopEntity carShopEntity = generateCarShopEntity(id, "AA-1");
        DealEntity dealEntity = generateDealEntity(id, carShopEntity, DealStatus.ACTIVE);
        Deal dealToCompare = generateDeal(id, DealStatus.ACTIVE, carSaleInfo);
        PersonEntity contact = new PersonEntity(id, "contact1");

        when(converter.domainToCarShopEntity(carSaleInfo)).thenReturn(carShopEntity);
        when(carService.checkPersonForExist("contact1")).thenReturn(contact);
        when(dealsDao.save(any(DealEntity.class))).thenReturn(dealEntity);
        when(converter.dealEntityToDeal(dealEntity)).thenReturn(dealToCompare);

        Optional<Deal> deal = dealService.addDeal(carSaleInfo, "contact1", 2000);

        verify(converter, times(1)).domainToCarShopEntity(carSaleInfo);
        verify(carService, times(1)).checkPersonForExist("contact1");
        verify(dealsDao, times(1)).save(any(DealEntity.class));
        verify(converter, times(1)).dealEntityToDeal(dealEntity);

        assertThat(deal.get()).isEqualTo(dealToCompare);
    }

    @Test
    public void shouldReturnEmptyWhenContactTheSame() {
        long id = 1L;
        CarSaleInfo car = generateCarSaleInfo(1L, "AA-1");
        CarShopEntity bmw = generateCarShopEntity(id, "AA-1");
        DealEntity dealEntityFirst = generateDealEntity(id, bmw, DealStatus.ACTIVE);

        when(converter.domainToCarShopEntity(car)).thenReturn(bmw);
        when(carService.checkPersonForExist("contact2")).thenReturn(new PersonEntity("contact2"));
        when(dealsDao.save(any(DealEntity.class))).thenReturn(dealEntityFirst);

        Optional<Deal> deal = dealService.addDeal(car, "contact", 2000);

        verify(converter, never()).domainToCarShopEntity(car);
        verify(carService, never()).checkPersonForExist("contact");
        verify(dealsDao, never()).save(any(DealEntity.class));
        assertThat(deal).isEqualTo(Optional.empty());
    }

    @Test
    public void shouldReturnDealCollectionWhenGetAllDeals() throws Exception {
        long id = 1L;
        DealEntity dealEntityFirst = generateDealEntity(1L, generateCarShopEntity(id, "AA-1"), DealStatus.ACTIVE);
        Deal dealFirst = generateDeal(1L, DealStatus.ACTIVE, generateCarSaleInfo(id, "AA-1"));

        List<DealEntity> dealEntities = Arrays.asList(dealEntityFirst);
        List<Deal> deals = Arrays.asList(dealFirst);

        when(dealsDao.findAll()).thenReturn(dealEntities);
        when(converter.DealEntitiesToDealsList(dealEntities)).thenReturn(deals);

        Collection<Deal> allDeals = dealService.getAllDeals();

        verify(dealsDao, times(1)).findAll();
        verify(converter, times(1)).DealEntitiesToDealsList(dealEntities);
        assertThat(allDeals).isEqualTo(deals);
    }

    @Test
    public void shouldRunMethodOnceGetAllDealsTheSameCar() throws Exception {
        long id = 1L;
        DealEntity dealEntityFirst = generateDealEntity(1L, generateCarShopEntity(id, "AA-1"), DealStatus.ACTIVE);
        DealEntity dealEntitySecond = generateDealEntity(2L, generateCarShopEntity(id, "AA-1"), DealStatus.ACTIVE);
        Deal dealFirst = generateDeal(1L, DealStatus.ACTIVE, generateCarSaleInfo(id, "AA-1"));
        Deal dealSecond = generateDeal(2L, DealStatus.ACTIVE, generateCarSaleInfo(id, "AA-1"));

        Collection<DealEntity> dealEntities = Arrays.asList(dealEntityFirst, dealEntitySecond);
        Collection<Deal> deals = Arrays.asList(dealFirst, dealSecond);

        when(dealsDao.getAllByCarShopEntityId(id)).thenReturn(dealEntities);
        when(converter.DealEntitiesToDealsList(dealEntities)).thenReturn(deals);

        Collection<Deal> allDealsTheSameCar = dealService.getAllDealsTheSameCar(id);

        verify(dealsDao, times(1)).getAllByCarShopEntityId(id);
        verify(converter, times(1)).DealEntitiesToDealsList(dealEntities);
        assertThat(allDealsTheSameCar).isEqualTo(deals);
    }

    @Test
    public void shouldAcceptDealAndRejectOtherDeals() throws Exception {
        long id = 1L;
        long dealIdFirst = 1L;
        long dealIdSecond = 2L;
        long dealIdThird = 3L;
        CarShopEntity bmw = generateCarShopEntity(id, "AA-1");
        DealEntity dealEntityFirst = generateDealEntity(dealIdFirst, bmw, DealStatus.ACTIVE);
        DealEntity dealEntitySecond = generateDealEntity(dealIdSecond, bmw, DealStatus.ACTIVE);
        DealEntity dealEntityThird = generateDealEntity(dealIdThird, bmw, DealStatus.ACTIVE);
        Collection<DealEntity> dealEntities = Arrays.asList(dealEntitySecond, dealEntityThird);

        when(dealsDao.findOne(id)).thenReturn(dealEntityFirst);
        when(dealsDao.getAllByCarShopEntityIdAndDealStatus(id, DealStatus.ACTIVE)).thenReturn(dealEntities);

        dealService.acceptDeal(id);

        assertThat(dealEntityFirst.getDealStatus()).isNotNull().isEqualTo(DealStatus.ACCEPTED);
        assertThat(dealEntitySecond.getDealStatus()).isNotNull().isEqualTo(DealStatus.REJECTED);
        assertThat(dealEntityThird.getDealStatus()).isNotNull().isEqualTo(DealStatus.REJECTED);
    }

    @Test
    public void shouldRunMethodOnceWhenRemoveDeal() throws Exception {
        long id = 1L;
        long dealIdFirst = 1L;
        CarShopEntity bmw = generateCarShopEntity(id, "AA-1");
        DealEntity dealEntityFirst = generateDealEntity(dealIdFirst, bmw, DealStatus.ACTIVE);

        when(dealsDao.findFirstByCarShopEntityId(id)).thenReturn(dealEntityFirst);

        dealService.removeDealByCarShopId(id);

        verify(dealsDao, times(1)).findFirstByCarShopEntityId(id);
        verify(dealsDao, times(1)).deleteDealEntitiesByCarShopEntityId(id);
    }

    @Test
    public void shouldNeverRunMethodWhenDealDoesNotExist() throws Exception {
        long id = 1L;

        when(dealsDao.findFirstByCarShopEntityId(id)).thenReturn(null);

        dealService.removeDealByCarShopId(id);

        verify(dealsDao, times(1)).findFirstByCarShopEntityId(id);
        verify(dealsDao, never()).deleteDealEntitiesByCarShopEntityId(id);
    }

    @Test
    public void shouldGetActiveDealStatus() {
        long id = 1L;
        long dealIdFirst = 1L;
        DealEntity dealEntity = generateDealEntity(dealIdFirst, generateCarShopEntity(id, "AA-1"), DealStatus.ACTIVE);

        when(dealsDao.findOne(id)).thenReturn(dealEntity);

        DealStatus dealStatus = dealService.getDealStatusById(dealIdFirst);

        assertThat(dealStatus).isEqualTo(DealStatus.ACTIVE);
    }

    @Test
    public void shouldReturnValidDealAfterGetBestDeal() {
        long id = 1L;
        long dealIdFirst = 1L;
        DealEntity dealEntity = generateDealEntity(dealIdFirst, generateCarShopEntity(id, "AA-1"), DealStatus.ACTIVE);
        Deal deal = generateDeal(dealIdFirst, DealStatus.ACCEPTED, generateCarSaleInfo(id, "AA-1"));

        when(dealsDao.getFirstByCarShopEntityIdOrderByBuyerPriceDesc(id)).thenReturn(dealEntity);
        when(converter.dealEntityToDeal(dealEntity)).thenReturn(deal);

        Deal dealToCompare = dealService.getBestDeal(id).get();

        verify(dealsDao, times(1)).getFirstByCarShopEntityIdOrderByBuyerPriceDesc(id);
        verify(converter, times(1)).dealEntityToDeal(dealEntity);
        assertThat(dealToCompare).isNotNull().isEqualTo(deal);
    }

    @Test
    public void shouldReturnFalseAfterCheckDealsForAccept() {
        long firstCar = 1L;
        long firstDeal = 1L;
        long secondDeal = 2L;
        Deal first = generateDeal(firstDeal, DealStatus.ACTIVE, generateCarSaleInfo(firstCar, "AA-1"));
        Deal second = generateDeal(secondDeal, DealStatus.ACTIVE, generateCarSaleInfo(firstCar, "AA-1"));
        Collection<Deal> allDealsTheSameCar = Arrays.asList(first, second);

        when(converter.DealEntitiesToDealsList(anyList())).thenReturn(allDealsTheSameCar);

        boolean check = dealService.checkDealForAccept(firstCar);

        assertThat(check).isFalse();
    }

    @Test
    public void shouldReturnTrueAfterCheckDealsForAccept() {
        long firstCar = 1L;
        long firstDeal = 1L;
        long secondDeal = 2L;
        Deal first = generateDeal(firstDeal, DealStatus.ACCEPTED, generateCarSaleInfo(firstCar, "AA-1"));
        Deal second = generateDeal(secondDeal, DealStatus.ACTIVE, generateCarSaleInfo(firstCar, "AA-1"));
        Collection<Deal> allDealsTheSameCar = Arrays.asList(first, second);

        when(converter.DealEntitiesToDealsList(anyList())).thenReturn(allDealsTheSameCar);

        boolean check = dealService.checkDealForAccept(firstCar);

        assertThat(check).isTrue();
    }
}