import java.io.File;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import attributes.Contract;
import attributes.Cover;
import attributes.Day;
import attributes.DayOff;
import attributes.DayOffWeekCover;
import attributes.Employee;
import attributes.Pattern;
import attributes.PatternEntry;
import attributes.SchedulingPeriod;
import attributes.Shift;
import attributes.ShiftOff;
import attributes.Skill;

public class XMLParser {
    private DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
    private DateFormat timeFormat = new SimpleDateFormat("HH:mm:ss");
    private String filePath;

    public XMLParser(String fileName) {
        filePath = "files/" + fileName + ".xml";
    }

    /**
     * Parst die XML-Datei und generiert die schedulingPeriod
     *
     * @return schedulingPeriod
     * @throws ParseException
     */
    public SchedulingPeriod parseXML() throws ParseException {
        Element rootElement = getElementFromXML();
        SchedulingPeriod schedulingPeriod = new SchedulingPeriod();

        //Set Header
        if (rootElement != null) {
            schedulingPeriod.setId(rootElement.getAttribute("ID"));
        }
        Date startDate = dateFormat.parse(getNodeName(rootElement,"StartDate"));
        Date endDate = dateFormat.parse(getNodeName(rootElement,"EndDate"));
        schedulingPeriod.setStartDate(startDate);
        schedulingPeriod.setEndDate(endDate);

        //Set Nodes
        parseSkills(rootElement, schedulingPeriod);
        String[] rootObjects = {"ShiftTypes", "Patterns", "Contracts", "Employees", "CoverRequirements", "DayOffRequests", "ShiftOffRequests"};
        String[] childObjects = {"Shift", "Pattern", "Contract", "Employee", "DayOfWeekCover", "DayOff", "ShiftOff"};
        for (int i = 0; i < rootObjects.length; i++) {
            parseObjects(rootElement, rootObjects[i], childObjects[i], schedulingPeriod);
        }
        return schedulingPeriod;
    }

    private void parseObjects(Element rootElement, String root, String child, SchedulingPeriod schedulingPeriod) throws ParseException {
        List<Object> objectList = new ArrayList<>();
        Node node = getNode(rootElement, root);
        Element element = (Element) node;
        NodeList nodeList = element.getElementsByTagName(child);
        for (int i = 0; i < nodeList.getLength(); i++) {
            Node currentNode = nodeList.item(i);
            Element e = (Element) currentNode;
            if (currentNode.getNodeType() == Node.ELEMENT_NODE) {
                switch (root) {
                    case "ShiftTypes":
                        objectList.add(parseShiftTypes(e));
                        break;
                    case "Patterns":
                        objectList.add(parsePatterns(e));
                        break;
                    case "Contracts":
                        objectList.add(parseContracts(e));
                        break;
                    case "Employees":
                        objectList.add(parseEmployees(e));
                        break;
                    case "CoverRequirements":
                        objectList.add(parseCoverRequirements(e));
                        break;
                    case "DayOffRequests":
                        objectList.add(parseDayOffRequests(e));
                        break;
                    case "ShiftOffRequests":
                        objectList.add(parseShiftOffRequests(e));
                        break;
                }
            }
        }
        switch (root) {
            case "ShiftTypes":
                schedulingPeriod.setShiftTypes(objectList);
                break;
            case "Patterns":
                schedulingPeriod.setPatterns(objectList);
                break;
            case "Contracts":
                schedulingPeriod.setContracts(objectList);
                break;
            case "Employees":
                schedulingPeriod.setEmployees(objectList);
                break;
            case "CoverRequirements":
                schedulingPeriod.setCoverRequirements(objectList);
                break;
            case "DayOffRequests":
                schedulingPeriod.setDayOffRequests(objectList);
                break;
            case "ShiftOffRequests":
                schedulingPeriod.setShiftOffRequests(objectList);
                break;
        }
    }

    /**
     * Parset die Vertragsdaten
     *
     * @param e Contract-Element
     * @return Contract
     */
    private Contract parseContracts(Element e) {
        Contract contract = new Contract();
        String root;
        String attr_weight = "weight";
        String attr_on = "on";
        contract.setId(Integer.parseInt(e.getAttribute("ID")));
        contract.setDescription(getNodeName(e,"Description"));

        root = "SingleAssignmentPerDay";
        contract.setSingleAssignmentPerDay(Boolean.parseBoolean(getNodeName(e, root)));
        contract.setSingleAssignmentPerDay_weight(Integer.parseInt(getAttributeValue(e, root, attr_weight)));

        root = "MaxNumAssignments";
        contract.setMaxNumAssignments(Integer.parseInt(getNodeName(e, root)));
        contract.setMaxNumAssignments_weight(Integer.parseInt(getAttributeValue(e, root, attr_weight)));
        contract.setMaxNumAssignments_on(Integer.parseInt(getAttributeValue(e, root, attr_on)));

        root = "MinNumAssignments";
        contract.setMinNumAssignments(Integer.parseInt(getNodeName(e, root)));
        contract.setMinNumAssignments_weight(Integer.parseInt(getAttributeValue(e, root, attr_weight)));
        contract.setMinNumAssignments_on(Integer.parseInt(getAttributeValue(e, root, attr_on)));

        root = "MaxConsecutiveWorkingDays";
        contract.setMaxConsecutiveWorkingDays(Integer.parseInt(getNodeName(e, root)));
        contract.setMaxConsecutiveWorkingDays_weight(Integer.parseInt(getAttributeValue(e, root, attr_weight)));
        contract.setMaxConsecutiveWorkingDays_on(Integer.parseInt(getAttributeValue(e, root, attr_on)));

        root = "MinConsecutiveWorkingDays";
        contract.setMinConsecutiveWorkingDays(Integer.parseInt(getNodeName(e, root)));
        contract.setMinConsecutiveWorkingDays_weight(Integer.parseInt(getAttributeValue(e, root, attr_weight)));
        contract.setMinConsecutiveWorkingDays_on(Integer.parseInt(getAttributeValue(e, root, attr_on)));

        root = "MaxConsecutiveFreeDays";
        contract.setMaxConsecutiveFreeDays(Integer.parseInt(getNodeName(e, root)));
        contract.setMaxConsecutiveFreeDays_weight(Integer.parseInt(getAttributeValue(e, root, attr_weight)));
        contract.setMaxConsecutiveFreeDays_on(Integer.parseInt(getAttributeValue(e, root, attr_on)));

        root = "MinConsecutiveFreeDays";
        contract.setMinConsecutiveFreeDays(Integer.parseInt(getNodeName(e, root)));
        contract.setMinConsecutiveFreeDays_weight(Integer.parseInt(getAttributeValue(e, root, attr_weight)));
        contract.setMinConsecutiveFreeDays_on(Integer.parseInt(getAttributeValue(e, root, attr_on)));

        root = "MaxConsecutiveWorkingWeekends";
        contract.setMaxConsecutiveWorkingWeekends(Integer.parseInt(getNodeName(e, root)));
        contract.setMaxConsecutiveWorkingWeekends_weight(Integer.parseInt(getAttributeValue(e, root, attr_weight)));
        contract.setMaxConsecutiveWorkingWeekends_on(Integer.parseInt(getAttributeValue(e, root, attr_on)));

        root = "MinConsecutiveWorkingWeekends";
        contract.setMinConsecutiveWorkingWeekends(Integer.parseInt(getNodeName(e, root)));
        contract.setMinConsecutiveWorkingWeekends_weight(Integer.parseInt(getAttributeValue(e, root, attr_weight)));
        contract.setMinConsecutiveWorkingWeekends_on(Integer.parseInt(getAttributeValue(e, root, attr_on)));

        root = "MaxWorkingWeekendsInFourWeeks";
        contract.setMaxWorkingWeekendsInFourWeeks(Integer.parseInt(getNodeName(e, root)));
        contract.setMaxWorkingWeekendsInFourWeeks_weight(Integer.parseInt(getAttributeValue(e, root, attr_weight)));
        contract.setMaxWorkingWeekendsInFourWeeks_on(Integer.parseInt(getAttributeValue(e, root, attr_on)));

        root = "WeekendDefinition";
        List<Day> weekendDefinition = checkWeekendDays(getNodeName(e, root));
        contract.setWeekendDefinition(weekendDefinition);

        root = "CompleteWeekends";
        contract.setCompleteWeekends(Boolean.parseBoolean(getNodeName(e, root)));
        contract.setCompleteWeekends_weight(Integer.parseInt(getAttributeValue(e, root, attr_weight)));

        root = "IdenticalShiftTypesDuringWeekend";
        contract.setIdenticalShiftTypesDuringWeekend(Boolean.parseBoolean(getNodeName(e, root)));
        contract.setIdenticalShiftTypesDuringWeekend_weight(Integer.parseInt(getAttributeValue(e, root, attr_weight)));

        root = "NoNightShiftBeforeFreeWeekend";
        contract.setNoNightShiftBeforeFreeWeekend(Boolean.parseBoolean(getNodeName(e, root)));
        contract.setNoNightShiftBeforeFreeWeekend_weight(Integer.parseInt(getAttributeValue(e, root, attr_weight)));

        root = "AlternativeSkillCategory";
        contract.setAlternativeSkillCategory(Boolean.parseBoolean(getNodeName(e, root)));
        contract.setAlternativeSkillCategory_weight(Integer.parseInt(getAttributeValue(e, root, attr_weight)));

        root = "UnwantedPatterns";
        List<Integer> unwantedPatterns = new ArrayList<>();
        Node node = getNode(e,root);
        Element element = (Element) node;
        NodeList nodeList = element.getElementsByTagName("Pattern");
        for (int i = 0; i < nodeList.getLength(); i++) {
            Node currentNode = nodeList.item(i);
            Element ee = (Element) currentNode;
            unwantedPatterns.add(Integer.parseInt(ee.getTextContent()));
          //  Pattern pattern = (Pattern) schedulingPeriod.getPatterns().get(0);
          //  pattern.getId()
        }
        contract.setUnwantedPatterns(unwantedPatterns);

        return contract;
    }

    /**
     * Erstellt eine Liste von Days, die der String days enthält
     *
     * @param days Sting mit Wochentagen
     * @return Liste der Wochentage
     */
    private List<Day> checkWeekendDays(String days) {
        List<Day> dayList = new ArrayList<>();
        if (days.contains(Day.Friday.name())) {
            dayList.add(Day.Friday);
        }
        if (days.contains(Day.Saturday.name())) {
            dayList.add(Day.Saturday);
        }
        if (days.contains(Day.Sunday.name())) {
            dayList.add(Day.Sunday);
        }
        return dayList;
    }

    private String getNodeName(Element e, String node) {
        Node contractNode = getNode(e, node);
        return contractNode.getTextContent();
    }

    private Node getNode(Element e, String node) {
        return e.getElementsByTagName(node).item(0);
    }

    private String getAttributeValue(Element e, String node, String attribute) {
        Node contractNode = getNode(e, node);
        Element contractElement = (Element) contractNode;
        return contractElement.getAttribute(attribute);
    }

    private Employee parseEmployees(Element e) {
        Employee employee = new Employee();
        employee.setId(Integer.parseInt(e.getAttribute("ID")));
        employee.setContractId(Integer.parseInt(getNodeName(e, "ContractID")));
        employee.setName(getNodeName(e, "Name"));
        Node skillNode = getNode(e, "Skills");
        Element skillElement = (Element) skillNode;
        NodeList skillNodeList = skillElement.getElementsByTagName("Skill");
        List<Skill> skillList = parseSkills(skillNodeList);
        employee.setSkills(skillList);
        return employee;
    }

    private DayOffWeekCover parseCoverRequirements(Element e) {
        DayOffWeekCover dayOffWeekCover = new DayOffWeekCover();
        String day = getNodeName(e, "Day");
        for (Object s : Day.values()) {
            if (day.equals(s.toString())) {
                dayOffWeekCover.setDay((Day) s);
            }
        }
        List<Cover> coverList = new ArrayList<>();
        NodeList nodeList = e.getElementsByTagName("Cover");
        for (int i = 0; i < nodeList.getLength(); i++) {
            Cover currentCover = new Cover();
            Node currentNode = nodeList.item(i);
            Element ee = (Element) currentNode;
            if (currentNode.getNodeType() == Node.ELEMENT_NODE) {
                currentCover.setShift(getNodeName(ee, "Shift"));
                currentCover.setPreferred(Integer.parseInt(getNodeName(ee, "Preferred")));
            }
            coverList.add(currentCover);
        }
        dayOffWeekCover.setCovers(coverList);

        return dayOffWeekCover;
    }

    private DayOff parseDayOffRequests(Element e) throws ParseException {
        DayOff dayOff = new DayOff();
        dayOff.setWeight(Integer.parseInt(e.getAttribute("weight")));
        dayOff.setEmployeeId(Integer.parseInt(getNodeName(e, "EmployeeID")));
        dayOff.setDate(dateFormat.parse(getNodeName(e, "Date")));
        return dayOff;
    }

    private ShiftOff parseShiftOffRequests(Element e) throws ParseException {
        ShiftOff shiftOff = new ShiftOff();
        shiftOff.setWeight(Integer.parseInt(e.getAttribute("weight")));
        shiftOff.setShiftTypeId(getNodeName(e,"ShiftTypeID"));
        shiftOff.setEmployeeId(Integer.parseInt(getNodeName(e,"EmployeeID")));
        shiftOff.setDate(dateFormat.parse(getNodeName(e,"Date")));
        return shiftOff;
    }

    private Pattern parsePatterns(Element e) {
        Pattern pattern = new Pattern();
        pattern.setId(Integer.parseInt(e.getAttribute("ID")));
        pattern.setWeight(Integer.parseInt(e.getAttribute("weight")));
        List<PatternEntry> patternEntryList = new ArrayList<>();
        Node patternNode = getNode(e,"PatternEntries");
        Element patternElement = (Element) patternNode;
        NodeList patternNodeList = patternElement.getElementsByTagName("PatternEntry");
        for (int j = 0; j < patternNodeList.getLength(); j++) {
            PatternEntry patternEntry = new PatternEntry();
            Node patternEntryNode = patternNodeList.item(j);
            if (patternEntryNode.getNodeType() == Node.ELEMENT_NODE) {
                Element patternEntryElement = (Element) patternEntryNode;
                patternEntry.setIndex(Integer.parseInt(patternEntryElement.getAttribute("index")));
                patternEntry.setShiftType(e.getElementsByTagName("ShiftType").item(j).getTextContent());
                String day = e.getElementsByTagName("Day").item(j).getTextContent();
                for (Object s : Day.values()) {
                    if (day.equals(s.toString())) {
                        patternEntry.setDay((Day) s);
                    }
                }
            }
            patternEntryList.add(patternEntry);
        }
        pattern.setPatternEntryList(patternEntryList);
        return pattern;
    }

    private void parseSkills(Element rootElement, SchedulingPeriod schedulingPeriod) {
        Node node = getNode(rootElement,"Skills");
        Element element = (Element) node;
        NodeList nodeList = element.getElementsByTagName("Skill");
        List<Skill> skillList = parseSkills(nodeList);
        schedulingPeriod.setSkills(skillList);
    }

    private Shift parseShiftTypes(Element e) throws ParseException {
        Shift shift = new Shift();
        shift.setId(e.getAttribute("ID"));
        shift.setStartTime(timeFormat.parse(getNodeName(e,"StartTime")));
        shift.setEndTime(timeFormat.parse(getNodeName(e,"EndTime")));
        shift.setDesciption(getNodeName(e,"Description"));
        Node skillNode = getNode(e, "Skills");
        Element skillElement = (Element) skillNode;
        NodeList skillNodeList = skillElement.getElementsByTagName("Skill");
        List<Skill> skillList = parseSkills(skillNodeList);
        shift.setSkills(skillList);
        return shift;
    }

    /**
     * Erstellt eine Liste der vorhandenen Skills
     *
     * @param skillNodeList Liste der Skills
     * @return Liste der vorhandenen Skills
     */
    private List<Skill> parseSkills(NodeList skillNodeList) {
        List<Skill> skillList = new ArrayList<>();
        for (int j = 0; j < skillNodeList.getLength(); j++) {
            String skill = skillNodeList.item(j).getTextContent();
            if (skill.equals("HeadNurse")) {
                skillList.add(Skill.HEAD_NURSE);
            } else if (skill.equals("Nurse")) {
                skillList.add(Skill.NURSE);
            }
        }
        return skillList;
    }

    /**
     * Liest die XML Datei ein
     *
     * @return Gibt das Element der XML Datei wieder
     */
    private Element getElementFromXML() {
        try {
            DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
            DocumentBuilder builder = factory.newDocumentBuilder();
            Document xmlDoc = builder.parse(new File(filePath));
            return xmlDoc.getDocumentElement();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }
}
