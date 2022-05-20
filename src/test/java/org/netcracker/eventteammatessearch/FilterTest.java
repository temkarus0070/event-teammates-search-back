package org.netcracker.eventteammatessearch;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.netcracker.eventteammatessearch.Services.EventsService;
import org.netcracker.eventteammatessearch.entity.*;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

public class FilterTest {
    private static EventsService eventsService;
    private static List<Event> eventList=new ArrayList<>();

    @BeforeAll
    public static void init(){
        eventsService=new EventsService();
        Event event = new Event(); //3 guests
        event.setId(1l);
        event.setDateTimeStart(LocalDateTime.of(2022,5,1,1,1,1));
        event.setDateTimeEnd(LocalDateTime.of(2022,5,5,1,1,1));
        event.setOnline(true);
        event.setGuests(Set.of(new EventAttendance(),new EventAttendance(),new EventAttendance()));
        event.setAvgMark(4);

        event.setEventType(new EventType("Деловые"));
        eventList.add(event);

        event = new Event(); //2 guest
        event.setId(2l);
        event.setDateTimeStart(LocalDateTime.of(2022,5,1,1,1,1));
        event.setDateTimeEnd(LocalDateTime.of(2022,5,4,1,1,1));
        event.setOnline(true);
        event.setGuests(Set.of(new EventAttendance(),new EventAttendance()));
        event.setAvgMark(3);
        event.setEventType(new EventType("Развлекательные"));
        eventList.add(event);

        event = new Event(); //2 guest
        event.setId(3l);
        event.setDateTimeStart(LocalDateTime.of(2022,5,10,1,1,1));
        event.setDateTimeEnd(LocalDateTime.of(2022,5,24,1,1,1));
        event.setOnline(false);
        event.setLocation(new Location("г.Воронеж,Транспортная улица,51"));
        event.setGuests(Set.of(new EventAttendance(),new EventAttendance()));
        event.setAvgMark(3);
        event.setEventType(new EventType("Развлекательные"));
        eventList.add(event);

        event = new Event(); //2 guest
        event.setId(4l);
        event.setDateTimeStart(LocalDateTime.of(2022,5,20,1,1,1));
        event.setDateTimeEnd(LocalDateTime.of(2022,5,25,1,1,1));
        event.setOnline(false);
        event.setLocation(new Location("г.Воронеж,Транспортная улица,51"));
        event.setGuests(Set.of(new EventAttendance(),new EventAttendance()));
        event.setAvgMark(1);
        event.setEventType(new EventType("Деловые"));
        eventList.add(event);

        event = new Event(); //paid 2 guest
        event.setId(5l);
        event.setPrice(1000);
        event.setDateTimeStart(LocalDateTime.of(2022,5,20,1,1,1));
        event.setDateTimeEnd(LocalDateTime.of(2022,5,25,1,1,1));
        event.setOnline(false);
        event.setLocation(new Location("г.Воронеж,Транспортная улица,51"));
        event.setGuests(Set.of(new EventAttendance(),new EventAttendance()));
        event.setAvgMark(1);
        event.setEventType(new EventType("Деловые"));
        eventList.add(event);


        event = new Event(); //paid 2 guest
        event.setId(6l);
        event.setPrice(100);
        event.setDateTimeStart(LocalDateTime.of(2022,5,20,1,1,1));
        event.setDateTimeEnd(LocalDateTime.of(2022,5,25,1,1,1));
        event.setOnline(false);
        event.setLocation(new Location("г.Воронеж,Транспортная улица,51"));
        event.setGuests(Set.of(new EventAttendance(),new EventAttendance()));
        event.setAvgMark(1);
        event.setEventType(new EventType("Деловые"));
        eventList.add(event);

        event = new Event(); //paid 1 guest
        event.setId(7l);
        event.setPrice(10);
        event.setDateTimeStart(LocalDateTime.of(2022,5,20,1,1,1));
        event.setDateTimeEnd(LocalDateTime.of(2022,5,25,1,1,1));
        event.setOnline(false);
        event.setLocation(new Location("г.Воронеж,Транспортная улица,51"));
        event.setGuests(Set.of(new EventAttendance()));
        event.setAvgMark(1);
        event.setEventType(new EventType("Деловые"));
        eventList.add(event);
    }

    @Test
    public void testLocationWithOnline(){
        Survey survey=new Survey();
        survey.setType(List.of("Развлекательные","Деловые"));
        survey.setFormat(List.of("Online","Offline"));
        survey.setDateTimeStart(LocalDateTime.of(2022,5,1,1,1,1));
        survey.setDateTimeEnd(LocalDateTime.of(2022,5,30,1,1));
        survey.setMinNumberOfGuests(0);
        survey.setMaxNumberOfGuests(100);
        survey.setMinPrice(0);
        survey.setMaxPrice(10000);
        survey.setLocation("Воронеж");
        List<Event> collect = eventList.stream().filter(e -> eventsService.isEventFitSurvey(e, survey)).collect(Collectors.toList());
        Assertions.assertEquals(7,collect.size());
    }

    @Test
    public void testLocationWithoutOnline(){
        Survey survey=new Survey();
        survey.setType(List.of("Развлекательные","Деловые"));
        survey.setFormat(List.of("Offline"));
        survey.setDateTimeStart(LocalDateTime.of(2022,5,1,1,1,1));
        survey.setDateTimeEnd(LocalDateTime.of(2022,5,30,1,1));
        survey.setMinNumberOfGuests(0);
        survey.setMaxNumberOfGuests(100);
        survey.setMinPrice(0);
        survey.setMaxPrice(10000);
        survey.setLocation("Воронеж");
        List<Event> collect = eventList.stream().filter(e -> eventsService.isEventFitSurvey(e, survey)).collect(Collectors.toList());
        Assertions.assertEquals(5,collect.size());
    }

    @Test
    public void testGuests(){
        Survey survey=new Survey();
        survey.setType(List.of("Развлекательные","Деловые"));
        survey.setFormat(List.of("Online","Offline"));
        survey.setDateTimeStart(LocalDateTime.of(2022,5,1,1,1,1));
        survey.setDateTimeEnd(LocalDateTime.of(2022,5,30,1,1));
        survey.setMinNumberOfGuests(1);
        survey.setMaxNumberOfGuests(2);
        survey.setMinPrice(0);
        survey.setMaxPrice(10000);
        survey.setLocation("Воронеж");
        List<Event> collect = eventList.stream().filter(e -> eventsService.isEventFitSurvey(e, survey)).collect(Collectors.toList());
        Assertions.assertEquals(6,collect.size());

        List<Long> collect1 = collect.stream().map(event -> event.getId()).sorted().collect(Collectors.toList());

        List list=new ArrayList();
        for (long l : IntStream.range(2, 8).asLongStream().toArray()) {
            list.add(l);
        }
        Assertions.assertIterableEquals(list,collect1);
    }


    @Test
    public void testGuestsLessThanTwo(){
        Survey survey=new Survey();
        survey.setType(List.of("Развлекательные","Деловые"));
        survey.setFormat(List.of("Online","Offline"));
        survey.setDateTimeStart(LocalDateTime.of(2022,5,1,1,1,1));
        survey.setDateTimeEnd(LocalDateTime.of(2022,5,30,1,1));
        survey.setMinNumberOfGuests(0);
        survey.setMaxNumberOfGuests(1);
        survey.setMinPrice(0);
        survey.setMaxPrice(10000);
        survey.setLocation("Воронеж");
        List<Event> collect = eventList.stream().filter(e -> eventsService.isEventFitSurvey(e, survey)).collect(Collectors.toList());
        Assertions.assertEquals(1,collect.size());
        List<Long> collect1 = collect.stream().map(event -> event.getId()).collect(Collectors.toList());
        collect1.get(0).equals(7l);
    }



    @Test
    public void testGuestsZero(){
        Survey survey=new Survey();
        survey.setType(List.of("Развлекательные","Деловые"));
        survey.setFormat(List.of("Online","Offline"));
        survey.setDateTimeStart(LocalDateTime.of(2022,5,1,1,1,1));
        survey.setDateTimeEnd(LocalDateTime.of(2022,5,30,1,1));
        survey.setMinNumberOfGuests(0);
        survey.setMaxNumberOfGuests(0);
        survey.setMinPrice(0);
        survey.setMaxPrice(10000);
        survey.setLocation("Воронеж");
        List<Event> collect = eventList.stream().filter(e -> eventsService.isEventFitSurvey(e, survey)).collect(Collectors.toList());
        Assertions.assertEquals(0,collect.size());

        Event event = new Event(); //3 guests
        event.setId(777l);
        event.setDateTimeStart(LocalDateTime.of(2022,5,10,1,1,1));
        event.setDateTimeEnd(LocalDateTime.of(2022,5,24,1,1,1));
        event.setOnline(true);
        event.setGuests(new HashSet<>());
        event.setAvgMark(4);

        event.setEventType(new EventType("Деловые"));
        eventList.add(event);

        collect = eventList.stream().filter(e -> eventsService.isEventFitSurvey(e, survey)).collect(Collectors.toList());
        Assertions.assertEquals(1,collect.size());
        Assertions.assertEquals(collect.get(0).getId(),777l);
        eventList.remove(event);

    }


    @Test
    public void testEventsTimeStartAndEnd(){
        Survey survey=new Survey();
        survey.setType(List.of("Развлекательные","Деловые"));
        survey.setFormat(List.of("Online","Offline"));
        survey.setDateTimeStart(LocalDateTime.of(2022,5,1,1,1,1));
        survey.setDateTimeEnd(LocalDateTime.of(2022,5,5,1,1,1));
        survey.setMinNumberOfGuests(0);
        survey.setMaxNumberOfGuests(555);
        survey.setMinPrice(0);
        survey.setMaxPrice(10000);
        survey.setLocation("Воронеж");
        List<Event> collect = eventList.stream().filter(e -> eventsService.isEventFitSurvey(e, survey)).collect(Collectors.toList());
        Assertions.assertEquals(2,collect.size());

        List<Long> collect1 = collect.stream().map(event -> event.getId()).sorted().collect(Collectors.toList());
        Assertions.assertIterableEquals(List.of(1l,2l),collect1);
    }

    @Test
    public void testEventsTimeStartAndEndFromFiveMay(){
        Survey survey=new Survey();
        survey.setType(List.of("Развлекательные","Деловые"));
        survey.setFormat(List.of("Online","Offline"));
        survey.setDateTimeStart(LocalDateTime.of(2022,5,5,1,1,1));
        survey.setDateTimeEnd(LocalDateTime.of(2022,5,30,1,1,1));
        survey.setMinNumberOfGuests(0);
        survey.setMaxNumberOfGuests(555);
        survey.setMinPrice(0);
        survey.setMaxPrice(10000);
        survey.setLocation("Воронеж");
        List<Event> collect = eventList.stream().filter(e -> eventsService.isEventFitSurvey(e, survey)).collect(Collectors.toList());
        Assertions.assertEquals(5,collect.size());

        List<Long> collect1 = collect.stream().map(event -> event.getId()).sorted().filter(e->e.equals(1l)||e.equals(2l)).collect(Collectors.toList());
        Assertions.assertEquals(0,collect1.size());
    }


    @Test
    public void testEventsByPrice(){
        Survey survey=new Survey();
        survey.setType(List.of("Развлекательные","Деловые"));
        survey.setFormat(List.of("Online","Offline"));
        survey.setDateTimeStart(LocalDateTime.of(2022,5,1,1,1,1));
        survey.setDateTimeEnd(LocalDateTime.of(2022,5,30,1,1,1));
        survey.setMinNumberOfGuests(0);
        survey.setMaxNumberOfGuests(555);
        survey.setMinPrice(9);
        survey.setMaxPrice(99);
        survey.setLocation("Воронеж");
        List<Event> collect = eventList.stream().filter(e -> eventsService.isEventFitSurvey(e, survey)).collect(Collectors.toList());
        Assertions.assertEquals(1,collect.size());
        Assertions.assertEquals(7l,collect.get(0).getId());

        survey.setMinPrice(100);
        survey.setMaxPrice(1000);
        collect = eventList.stream().filter(e -> eventsService.isEventFitSurvey(e, survey)).collect(Collectors.toList());
        Assertions.assertEquals(2,collect.size());
        List<Long> ids = collect.stream().map(e -> e.getId()).sorted().collect(Collectors.toList());
        Assertions.assertIterableEquals(List.of(5l,6l),ids);
    }


    @Test
    public void testEventsByTypes(){
        Survey survey=new Survey();
        survey.setType(List.of("Развлекательные"));
        survey.setFormat(List.of("Online","Offline"));
        survey.setDateTimeStart(LocalDateTime.of(2022,5,1,1,1,1));
        survey.setDateTimeEnd(LocalDateTime.of(2022,5,30,1,1,1));
        survey.setMinNumberOfGuests(0);
        survey.setMaxNumberOfGuests(555);
        survey.setMinPrice(0);
        survey.setMaxPrice(99999);
        survey.setLocation("Воронеж");
        List<Event> collect = eventList.stream().filter(e -> eventsService.isEventFitSurvey(e, survey)).collect(Collectors.toList());
        Assertions.assertEquals(2,collect.size());
        List<Long> ids = collect.stream().map(e -> e.getId()).sorted().collect(Collectors.toList());
        Assertions.assertIterableEquals(List.of(2l,3l),ids);
    }
}
