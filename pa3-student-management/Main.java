import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;
import java.util.TreeSet;

// Veri yükleme sırasında geçersiz kişi türleri için özel istisna sınıfı.
class InvalidPersonTypeException extends Exception {
    public InvalidPersonTypeException(String message) {
        super(message);
    }
}

// Bir kişi (öğrenci veya öğretim üyesi) bulunamadığında fırlatılan özel istisna.
class NonExistentPersonException extends Exception {
    public NonExistentPersonException(String message) {
        super(message);
    }
}

// Belirtilen bir akademik departman bulunamadığında fırlatılan özel istisna.
class NonExistentDepartmentException extends Exception {
    public NonExistentDepartmentException(String message) {
        super(message);
    }
}

// Belirtilen bir akademik program bulunamadığında fırlatılan özel istisna.
class NonExistentProgramException extends Exception {
    public NonExistentProgramException(String message) {
        super(message);
    }
}

// Belirtilen bir ders bulunamadığında fırlatılan özel istisna.
class NonExistentCourseException extends Exception {
    public NonExistentCourseException(String message) {
        super(message);
    }
}

// Geçersiz not değerleri için özel istisna sınıfı.
class InvalidGradeException extends Exception {
    public InvalidGradeException(String message) {
        super(message);
    }
}

// Rapor girdisi üretebilen varlıklar için arayüz.
interface Reportable {
    String generateReportEntry();
}

// Sistemdeki tüm kişiler (öğrenciler, akademisyenler) için soyut temel sınıf.
abstract class Person implements Reportable, Comparable<Person> {
    protected String id; // Kişinin benzersiz kimliği.
    protected String name; // Kişinin tam adı.
    protected String email; // Kişinin e-posta adresi.
    protected String departmentOrMajorName; // Akademisyen için bölüm, öğrenci için ana dal adı.

    // Bir kişinin ortak özelliklerini başlatan yapıcı metot.
    public Person(String id, String name, String email, String d) {
        this.id = id;
        this.name = name;
        this.email = email;
        this.departmentOrMajorName = d;
    }

    // Kişinin kimliğini döndürür.
    public String getId() {
        return id;
    }

    // Kişinin adını döndürür.
    public String getName() {
        return name;
    }

    // Kişileri kimliklerine göre sıralamak için karşılaştırır.
    @Override
    public int compareTo(Person other) {
        return this.id.compareTo(other.id);
    }

    // Kişinin kimliğine göre eşitliği kontrol eder.
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Person person = (Person) o;
        return id.equals(person.id);
    }

    // Kişinin kimliğine göre bir karma kod üretir.
    @Override
    public int hashCode() {
        return id.hashCode();
    }
}

// Dersler, bölümler ve programlar gibi akademik varlıklar için soyut temel sınıf.
abstract class AcademicEntity implements Reportable, Comparable<AcademicEntity> {
    protected String code; // Akademik varlığın benzersiz kodu.
    protected String name; // Akademik varlığın adı.
    protected String description; // Varlık için isteğe bağlı açıklama.

    // Bir akademik varlığı kod, ad ve açıklama ile başlatan yapıcı metot.
    public AcademicEntity(String code, String name, String d) {
        this.code = code;
        this.name = name;
        this.description = d;
    }

    // Ayrıntılı açıklaması olmayan akademik varlıklar için aşırı yüklenmiş yapıcı metot.
    public AcademicEntity(String code, String name) {
        this(code, name, ""); // Ana yapıcıyı boş açıklama ile çağırır.
    }

    // Akademik varlığın kodunu döndürür.
    public String getCode() {
        return code;
    }

    // Akademik varlığın adını döndürür.
    public String getName() {
        return name;
    }

    // Akademik varlıkları kodlarına göre sıralamak için karşılaştırır.
    @Override
    public int compareTo(AcademicEntity other) {
        return this.code.compareTo(other.code);
    }

    // Akademik varlığın koduna göre eşitliği kontrol eder.
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        AcademicEntity that = (AcademicEntity) o;
        return code.equals(that.code);
    }

    // Akademik varlığın koduna göre bir karma kod üretir.
    @Override
    public int hashCode() {
        return code.hashCode();
    }
}

// Sistemde bir öğrenciyi temsil eder, Person sınıfından miras alır.
class Student extends Person {
    private final String status = "Active"; // Bir öğrenci için varsayılan durum.
    private final Set<Course> enrolledCourses = new TreeSet<>(); // Öğrencinin kayıtlı olduğu dersler (ders koduna göre sıralı).
    private final Map<Course, String> completedCoursesWithGrades = new LinkedHashMap<>(); // Öğrencinin tamamladığı dersler ve notları (eklenme sırasına göre).
    private double gpa = 0.0; // Öğrencinin Not Ortalaması.

    // Bir öğrenciyi başlatan yapıcı metot.
    public Student(String id, String n, String e, String m) {
        super(id, n, e, m); // Person sınıfının yapıcısını çağırır.
    }

    // Bir öğrenciyi, daha önce tamamlamadıysa bir derse kaydeder.
    public void enrollCourse(Course c) {
        if (!completedCoursesWithGrades.containsKey(c)) {
            enrolledCourses.add(c);
        }
    }

    // Tamamlanmış bir dersi ve notunu öğrencinin kaydına ekler.
    public void addCompletedCourse(Course c, String g) {
        completedCoursesWithGrades.put(c, g);
        enrolledCourses.remove(c); // Dersi mevcut kayıtlı dersler listesinden kaldırır.
    }

    // Öğrencinin not ortalamasını (GPA) tamamlanmış derslere ve not-puan eşlemesine göre hesaplar.
    public void calculateGPA(Map<String, Double> gradePointsMap) {
        if (completedCoursesWithGrades.isEmpty()) {
            this.gpa = 0.0; // Tamamlanmış ders yoksa GPA 0'dır.
            return;
        }
        double totalPoints = 0; // Kazanılan toplam not puanı.
        int totalCredits = 0; // Notlandırılmış derslerin toplam kredisi.
        for (Map.Entry<Course, String> entry : completedCoursesWithGrades.entrySet()) {
            Double points = gradePointsMap.get(entry.getValue()); // Harf notu için not puanını alır.
            if (points != null) { // Sadece geçerli notların GPA'ya katkıda bulunmasını sağlar.
                totalPoints += points * entry.getKey().getCredits();
                totalCredits += entry.getKey().getCredits();
            }
        }
        if (totalCredits == 0) { // Geçerli kredi yoksa sıfıra bölme hatasını önler.
            this.gpa = 0.0;
        } else {
            this.gpa = totalPoints / totalCredits;
        }
    }

    // Öğrenci için kısa bir rapor girdisi üretir.
    @Override
    public String generateReportEntry() {
        return String.format("Student ID: %s\nName: %s\nEmail: %s\nMajor: %s\nStatus: %s",
                id, name, email, departmentOrMajorName, status);
    }

    // Dersler ve GPA dahil öğrenci için tam ve ayrıntılı bir rapor üretir.
    public String generateFullReport(Map<String, Double> gradePointsMap) {
        calculateGPA(gradePointsMap); // Raporu oluşturmadan önce GPA'nın güncel olmasını sağlar.
        DecimalFormat df = new DecimalFormat("0.00"); // GPA'yı iki ondalık basamağa formatlar.
        StringBuilder sb = new StringBuilder();
        sb.append(String.format("Student ID: %s\nName: %s\nEmail: %s\nMajor: %s\nStatus: %s\n\n",
                id, name, email, departmentOrMajorName, status));
        sb.append("Enrolled Courses:\n");
        if (!enrolledCourses.isEmpty()) {
            for (Course c : enrolledCourses) {
                sb.append(String.format("- %s (%s)\n", c.getName(), c.getCode()));
            }
        }
        sb.append("\nCompleted Courses:\n");
        if (!completedCoursesWithGrades.isEmpty()) {
            for (Map.Entry<Course, String> entry : completedCoursesWithGrades.entrySet()) {
                Course c = entry.getKey();
                String g = entry.getValue();
                sb.append(String.format("- %s (%s): %s\n", c.getName(), c.getCode(), g));
            }
        }
        sb.append("\nGPA: ").append(df.format(gpa)).append("\n");
        return sb.toString();
    }
}

// Sistemde bir akademik üyeyi (öğretim üyesi) temsil eder.
class AcademicMember extends Person {
    // Bir akademik üyeyi başlatan yapıcı metot.
    public AcademicMember(String id, String n, String e, String d) {
        super(id, n, e, d); // Person sınıfının yapıcısını çağırır.
    }

    // Akademik üye için kısa bir rapor girdisi üretir.
    @Override
    public String generateReportEntry() {
        return String.format("Faculty ID: %s\nName: %s\nEmail: %s\nDepartment: %s",
                id, name, email, departmentOrMajorName);
    }
}

// Akademik sistemde sunulan bir dersi temsil eder.
class Course extends AcademicEntity {
    private final String departmentNameForCourse; // Bu dersi sunan bölüm.
    private final int credits; // Bu dersin kredi sayısı.
    private final String semester; // Dersin genellikle sunulduğu dönem.
    private AcademicMember instructor; // Bu dersi veren akademik üye.
    private final Set<Student> enrolledStudentsSet = new TreeSet<>(); // Bu derse kayıtlı öğrenciler (öğrenci ID'sine göre sıralı).
    private final Map<String, String> studentGrades = new HashMap<>(); // Öğrenci ID'lerini bu dersteki harf notlarına eşler.
    private final Map<String, Integer> gradeDistribution = new TreeMap<>(); // Verilen her notun sayısını izler (nota göre sıralı).
    private double averageGrade = 0.0; // Bu dersin ortalama notu (puan tabanlı).

    // Bir dersi başlatan yapıcı metot.
    public Course(String c, String n, String dName, int cred, String sem, String progAff) {
        super(c, n); // AcademicEntity yapıcısını çağırır.
        this.departmentNameForCourse = dName;
        this.credits = cred;
        this.semester = sem;
    }

    // Dersin kredi değerini döndürür.
    public int getCredits() {
        return credits;
    }

    // Bu dersin eğitmenini ayarlar.
    public void setInstructor(AcademicMember i) {
        this.instructor = i;
    }

    // Bu ders için kayıtlı öğrenciler listesine bir öğrenci ekler.
    public void addEnrolledStudent(Student s) {
        enrolledStudentsSet.add(s);
    }

    // Bir öğrencinin bu ders için notunu kaydeder ve not dağılımını ve ortalamasını günceller.
    public void addGrade(String studentId, String letterGrade, Map<String, Double> gradePointsMap) {
        studentGrades.put(studentId, letterGrade);
        gradeDistribution.put(letterGrade, gradeDistribution.getOrDefault(letterGrade, 0) + 1);
        calculateAverageGrade(gradePointsMap); // Yeni bir not eklendikten sonra ders ortalama notunu yeniden hesaplar.
    }

    // Dersin ortalama notunu gönderilen öğrenci notlarına göre hesaplar.
    public void calculateAverageGrade(Map<String, Double> gradePointsMap) {
        if (studentGrades.isEmpty()) {
            this.averageGrade = 0.0; // Kayıtlı not yoksa ortalama 0'dır.
            return;
        }
        double totalPoints = 0; // Dersteki tüm öğrenciler için toplam not puanı.
        for (String gradeValue : studentGrades.values()) {
            totalPoints += gradePointsMap.getOrDefault(gradeValue, 0.0); // Harf notunu not puanına dönüştürür.
        }
        this.averageGrade = totalPoints / studentGrades.size();
    }

    // Ders için kısa bir rapor girdisi üretir.
    @Override
    public String generateReportEntry() {
        return String.format("Course Code: %s\nName: %s\nDepartment: %s\nCredits: %d\nSemester: %s",
                code, name, departmentNameForCourse, credits, semester);
    }

    // Eğitmen ve öğrenci istatistikleri dahil ders için tam ve ayrıntılı bir rapor üretir.
    public String generateFullReport() {
        DecimalFormat df = new DecimalFormat("0.00"); // Ortalama notu iki ondalık basamağa formatlar.
        StringBuilder sb = new StringBuilder();
        sb.append(String.format("Course Code: %s\nName: %s\nDepartment: %s\nCredits: %d\nSemester: %s\n\n",
                code, name, departmentNameForCourse, credits, semester));
        sb.append("Instructor: ").append(instructor != null ? instructor.getName() : "Not assigned").append("\n\n");
        sb.append("Enrolled Students:\n");
        if (!enrolledStudentsSet.isEmpty()) {
            for (Student s : enrolledStudentsSet) {
                sb.append(String.format("- %s (ID: %s)\n", s.getName(), s.getId()));
            }
        }
        sb.append("\nGrade Distribution:\n");
        if (!gradeDistribution.isEmpty()) {
            for (Map.Entry<String, Integer> entry : gradeDistribution.entrySet()) {
                sb.append(String.format("%s: %d\n", entry.getKey(), entry.getValue()));
            }
        }
        sb.append("\nAverage Grade: ").append(df.format(this.averageGrade)).append("\n");
        return sb.toString();
    }
}

// Kurum içindeki bir akademik bölümü temsil eder.
class Department extends AcademicEntity {
    private AcademicMember head; // Bu bölümün başkanı olan akademik üye.
    private final String headIdFromFile; // Dosyadan okunan ve daha sonra çözümlenecek olan bölüm başkanının ID'si.

    // Bir bölümü başlatan yapıcı metot.
    public Department(String c, String n, String d, String hId) {
        super(c, n, d); // AcademicEntity yapıcısını çağırır.
        this.headIdFromFile = hId; // Başkan ID'sini daha sonra bağlamak için saklar.
    }

    // Giriş dosyasından okunan başkan ID'sini döndürür.
    public String getHeadIdFromFile() {
        return headIdFromFile;
    }

    // Bölüm başkanını ayarlar.
    public void setHead(AcademicMember h) {
        this.head = h;
    }

    // Bölüm için kısa bir rapor girdisi üretir.
    @Override
    public String generateReportEntry() {
        return String.format("Department Code: %s\nName: %s\nHead: %s",
                code, name, (head != null ? head.getName() : "Not assigned"));
    }
}

// Bir bölüm tarafından sunulan bir akademik programı temsil eder.
class Program extends AcademicEntity {
    private final String departmentNameForProgram; // Bu programın ait olduğu bölümün adı.
    private Department department; // Bu programın ilişkili olduğu gerçek Department nesnesi.
    private final String degreeLevel; // Örn: "Bachelor", "Master".
    private final int totalCreditsRequired; // Programı tamamlamak için gereken toplam kredi.
    private final List<Course> coursesInProgram = new ArrayList<>(); // Bu programın bir parçası olan derslerin listesi.

    // Bir akademik programı başlatan yapıcı metot.
    public Program(String c, String n, String desc, String dName, String dl, int tc) {
        super(c, n, desc); // AcademicEntity yapıcısını çağırır.
        this.departmentNameForProgram = dName;
        this.degreeLevel = dl;
        this.totalCreditsRequired = tc;
    }

    // Bu programla ilişkili bölümün adını döndürür.
    public String getDepartmentNameForProgram() {
        return departmentNameForProgram;
    }

    // Bu programı ilgili Department nesnesine bağlar.
    public void setDepartment(Department d) {
        this.department = d;
    }

    // Bu programın müfredatına, zaten mevcut değilse bir ders ekler.
    public void addCourseToProgram(Course course) {
        if (!coursesInProgram.contains(course)) {
            coursesInProgram.add(course);
        }
    }

    // Dersleri dahil program için kısa bir rapor girdisi üretir.
    @Override
    public String generateReportEntry() {
        StringBuilder sb = new StringBuilder();
        sb.append(String.format("Program Code: %s\nName: %s\nDepartment: %s\nDegree Level: %s\nRequired Credits: %d\n",
                code, name, (department != null ? department.getName() : departmentNameForProgram), degreeLevel,
                totalCreditsRequired));
        sb.append("Courses: ");
        if (coursesInProgram.isEmpty()) {
            sb.append("-\n"); // Liste boşsa ders olmadığını belirtir.
        } else {
            sb.append("{");
            Collections.sort(coursesInProgram); // Yazdırmadan önce dersleri koda göre sıralar.
            for (int i = 0; i < coursesInProgram.size(); i++) {
                sb.append(coursesInProgram.get(i).getCode()); // Ders kodlarını ekler.
                if (i < coursesInProgram.size() - 1) {
                    sb.append(","); // Ders kodlarını virgülle ayırır.
                }
            }
            sb.append("}\n");
        }
        return sb.toString();
    }
}

// Tüm öğrenci, akademik ve dersle ilgili veri ve işlemleri yönetir.
class StudentManagementSystem {
    private final Map<String, Student> studentsMap = new TreeMap<>();
    private final Map<String, AcademicMember> academicMembersMap = new TreeMap<>();
    private final Map<String, Department> departmentsMap = new TreeMap<>();
    private final Map<String, Program> programsMap = new TreeMap<>();
    private final Map<String, Course> coursesMap = new TreeMap<>();
    private final StringBuilder consoleAndErrorOutput = new StringBuilder(); // Tüm işlem mesajlarını ve hatalarını çıktı için toplar.

    // Her harf notu için puan değerini tanımlayan statik harita.
    private static final Map<String, Double> GRADE_POINTS = new HashMap<>();
    static { // Not puanlarını doldurmak için statik başlatıcı bloğu.
        GRADE_POINTS.put("A1", 4.00); GRADE_POINTS.put("A2", 3.50);
        GRADE_POINTS.put("B1", 3.00); GRADE_POINTS.put("B2", 2.50);
        GRADE_POINTS.put("C1", 2.00); GRADE_POINTS.put("C2", 1.50);
        GRADE_POINTS.put("D1", 1.00); GRADE_POINTS.put("D2", 0.50);
        GRADE_POINTS.put("F3", 0.00);
    }

    // Belirtilen bir dosya yolundan tüm satırları okumak için yardımcı metot.
    private List<String> readFile(String filePath) throws IOException {
        return Files.readAllLines(Paths.get(filePath));
    }

    // Kişi (öğrenci ve öğretim üyesi) verilerini bir CSV dosyasından yükler.
    public void loadPersons(String filePath) {
        consoleAndErrorOutput.append("Reading Person Information\n");
        try {
            List<String> lines = readFile(filePath);
            for (String line : lines) {
                try {
                    String[] parts = line.split(",");
                    if (parts.length != 5) continue; // Hatalı biçimlendirilmiş satırı atla.
                    String type = parts[0].trim(); String id = parts[1].trim();
                    String name = parts[2].trim(); String email = parts[3].trim();
                    String val5 = parts[4].trim();

                    if ("S".equalsIgnoreCase(type)) {
                        studentsMap.put(id, new Student(id, name, email, val5));
                    } else if ("F".equalsIgnoreCase(type)) {
                        academicMembersMap.put(id, new AcademicMember(id, name, email, val5));
                    } else {
                        throw new InvalidPersonTypeException("Invalid Person Type");
                    }
                } catch (InvalidPersonTypeException e) {
                    consoleAndErrorOutput.append(e.getMessage()).append("\n");
                } catch (Exception e) { /* Genel hataları sessizce işle veya logla */ }
            }
        } catch (IOException e) { /* Dosya okuma hatasını işle */ }
    }

    // Bölüm verilerini bir CSV dosyasından yükler.
    public void loadDepartments(String filePath) {
        consoleAndErrorOutput.append("Reading Departments\n");
        try {
            List<String> lines = readFile(filePath);
            for (String line : lines) {
                try {
                    String[] parts = line.split(",");
                    if (parts.length != 4) continue;
                    departmentsMap.put(parts[0].trim(), new Department(parts[0].trim(), parts[1].trim(), parts[2].trim(), parts[3].trim()));
                } catch (Exception e) { /* Satır işleme hatası */ }
            }
        } catch (IOException e) { /* Dosya okuma hatası (isteğe bağlı dosya) */ }
    }

    // Bölüm başkanı ID'lerini gerçek AcademicMember nesnelerine çözümler.
    public void resolveDepartmentHeads() {
        for (Department dept : departmentsMap.values()) {
            String headId = dept.getHeadIdFromFile();
            if (headId != null && !headId.isEmpty()) {
                AcademicMember head = academicMembersMap.get(headId);
                if (head != null) {
                    dept.setHead(head);
                } else {
                    consoleAndErrorOutput.append("Academic Member Not Found with ID ").append(headId).append("\n");
                }
            }
        }
    }

    // Akademik program verilerini bir CSV dosyasından yükler ve bölümlere bağlar.
    public void loadPrograms(String filePath) {
        consoleAndErrorOutput.append("Reading Programs\n");
        try {
            List<String> lines = readFile(filePath);
            for (String line : lines) {
                try {
                    String[] parts = line.split(",");
                    if (parts.length != 6) continue;
                    String code = parts[0].trim(); String name = parts[1].trim(); String desc = parts[2].trim();
                    String deptName = parts[3].trim(); String degreeL = parts[4].trim(); int credits = Integer.parseInt(parts[5].trim());
                    Program program = new Program(code, name, desc, deptName, degreeL, credits);
                    Department linkedDept = departmentsMap.values().stream()
                        .filter(d -> d.getName().equalsIgnoreCase(deptName)).findFirst().orElse(null);
                    
                    if (linkedDept != null) {
                        program.setDepartment(linkedDept);
                        programsMap.put(code, program);
                    } else {
                         throw new NonExistentDepartmentException("Department '" + deptName + "' Not Found");
                    }
                } catch (NumberFormatException e) { /* Kredi için geçersiz sayı formatı */ }
                  catch (NonExistentDepartmentException e) { consoleAndErrorOutput.append(e.getMessage()).append("\n"); }
                  catch (Exception e) { /* Genel satır işleme hatası */ }
            }
        } catch (IOException e) { /* Dosya okuma hatası */ }
    }

    // Ders verilerini bir CSV dosyasından yükler ve belirtilmişse programlara bağlar.
    public void loadCourses(String filePath) {
        consoleAndErrorOutput.append("Reading Courses\n");
        try {
            List<String> lines = readFile(filePath);
            for (String line : lines) {
                try {
                    String[] parts = line.split(",");
                    if (parts.length < 5 || parts.length > 6) continue;
                    String code = parts[0].trim(); String name = parts[1].trim(); String deptN = parts[2].trim();
                    int credits = Integer.parseInt(parts[3].trim()); String sem = parts[4].trim();
                    String progAff = (parts.length > 5 && parts[5] != null && !parts[5].trim().isEmpty()) ? parts[5].trim() : null;
                    
                    Course course = new Course(code, name, deptN, credits, sem, progAff);
                    if (progAff != null) {
                        Program program = programsMap.get(progAff);
                        if (program == null) {
                            throw new NonExistentProgramException("Program " + progAff + " Not Found");
                        }
                        program.addCourseToProgram(course);
                    }
                    coursesMap.put(code, course); // Program bağlantısı olmasa bile dersi ekle
                } catch (NumberFormatException e) { /* Kredi için geçersiz sayı formatı */ }
                  catch (NonExistentProgramException e) { consoleAndErrorOutput.append(e.getMessage()).append("\n"); }
                  catch (Exception e) { /* Genel satır işleme hatası */ }
            }
        } catch (IOException e) { /* Dosya okuma hatası */ }
    }

    // Ders atamalarını (eğitmen-ders, öğrenci-ders kayıtları) bir CSV dosyasından yükler.
    public void loadAssignments(String filePath) {
        consoleAndErrorOutput.append("Reading Course Assignments\n");
        try {
            List<String> lines = readFile(filePath);
            for (String line : lines) {
                try {
                    String[] parts = line.split(",");
                    if (parts.length != 3) continue;
                    String pType = parts[0].trim(); String pId = parts[1].trim(); String cCode = parts[2].trim();

                    if ("F".equalsIgnoreCase(pType)) {
                        AcademicMember member = academicMembersMap.get(pId);
                        Course course = coursesMap.get(cCode);
                        if (member == null) throw new NonExistentPersonException("Academic Member Not Found with ID " + pId);
                        if (course == null) throw new NonExistentCourseException("Course " + cCode + " Not Found");
                        course.setInstructor(member);
                    } else if ("S".equalsIgnoreCase(pType)) {
                        Student student = studentsMap.get(pId);
                        Course course = coursesMap.get(cCode);
                        if (student == null) throw new NonExistentPersonException("Student Not Found with ID " + pId);
                        if (course == null) throw new NonExistentCourseException("Course " + cCode + " Not Found");
                        student.enrollCourse(course);
                        course.addEnrolledStudent(student);
                    } else { /* Geçersiz kişi tipi, loglanabilir */ }
                } catch (NonExistentPersonException | NonExistentCourseException e) {
                    consoleAndErrorOutput.append(e.getMessage()).append("\n");
                } catch (Exception e) { /* Genel satır işleme hatası */ }
            }
        } catch (IOException e) { /* Dosya okuma hatası */ }
    }

    // Öğrenci notlarını dersler için bir CSV dosyasından yükler.
    public void loadGrades(String filePath) {
        consoleAndErrorOutput.append("Reading Grades\n");
        try {
            List<String> lines = readFile(filePath);
            for (String line : lines) {
                try {
                    String[] parts = line.split(",");
                    if (parts.length != 3) continue;
                    String lGrade = parts[0].trim(); String sId = parts[1].trim(); String cCode = parts[2].trim();

                    Student student = studentsMap.get(sId);
                    Course course = coursesMap.get(cCode);

                    if (student == null) throw new NonExistentPersonException("Student Not Found with ID " + sId);
                    if (course == null) throw new NonExistentCourseException("Course " + cCode + " Not Found");
                    
                    if (!GRADE_POINTS.containsKey(lGrade)) {
                        throw new InvalidGradeException("The grade " + lGrade + " is not valid");
                    }
                    course.addGrade(sId, lGrade, GRADE_POINTS);
                    student.addCompletedCourse(course, lGrade);
                } catch (NonExistentPersonException | NonExistentCourseException | InvalidGradeException e) {
                    consoleAndErrorOutput.append(e.getMessage()).append("\n");
                } catch (Exception e) { /* Genel satır işleme hatası */ }
            }
        } catch (IOException e) { /* Dosya okuma hatası */ }
        studentsMap.values().forEach(s -> s.calculateGPA(GRADE_POINTS)); // Tüm öğrencilerin GPA'larını hesapla.
    }

    // Sistemdeki tüm varlıklar için kapsamlı raporlar içeren bir string üretir.
    public String generateReports() {
        StringBuilder r = new StringBuilder();
        String sep = "----------------------------------------\n";
        String titleSep = "---------------------------------------\n"; // DEPARTMENTS, PROGRAMS, COURSES (özet) için

        r.append(sep).append("            Academic Members\n").append(sep);
        if (academicMembersMap.isEmpty()) r.append("-\n\n");
        else academicMembersMap.values().forEach(am -> r.append(am.generateReportEntry()).append("\n\n"));
        if (!academicMembersMap.isEmpty()) r.deleteCharAt(r.length()-1); // Son fazla \n'i sil
        r.append(sep);
        
        r.append("                STUDENTS\n").append(sep);
        if (studentsMap.isEmpty()) r.append("-\n\n");
        else studentsMap.values().forEach(s -> r.append(s.generateReportEntry()).append("\n\n"));
        if (!studentsMap.isEmpty()) r.deleteCharAt(r.length()-1);
        r.append(sep);

        // Departmanlar, Programlar, Kurslar için başlık formatları ve ayırıcılar farklı olabilir.
        // Doğru çıktıdaki ayırıcıları tam eşleştirmek için dikkatli olmak gerekiyor.
        // Doğru çıktıda DEPARTMENTS başlığının üstünde ve altında titleSep'e benzeyen bir şey var.
        
        String departmentTitleSeparator = "---------------------------------------\n"; // DEPARTMENTS için
        r.append(departmentTitleSeparator).append("              DEPARTMENTS\n").append(departmentTitleSeparator);
        if (departmentsMap.isEmpty()) r.append("-\n\n");
        else departmentsMap.values().forEach(d -> r.append(d.generateReportEntry()).append("\n\n"));
        if (!departmentsMap.isEmpty()) r.deleteCharAt(r.length()-1);
        r.append(sep);


        String programTitleSeparator = "--------------------------------------\n"; // PROGRAMS için (bir tire eksik)
        r.append(programTitleSeparator).append("                PROGRAMS\n").append(departmentTitleSeparator); // Altındaki titleSep ile aynı
        if (programsMap.isEmpty()) r.append("-\n\n");
        else programsMap.values().forEach(p -> r.append(p.generateReportEntry()).append("\n")); // Program raporu zaten \n ile bitiyor
        if (!programsMap.isEmpty() && !r.toString().endsWith("\n\n")) r.append("\n"); // Son \n eksikse ekle
        r.append(sep);
        
        r.append(departmentTitleSeparator).append("                COURSES\n").append(departmentTitleSeparator);
        if (coursesMap.isEmpty()) r.append("-\n\n");
        else coursesMap.values().forEach(c -> r.append(c.generateReportEntry()).append("\n\n"));
        if (!coursesMap.isEmpty()) r.deleteCharAt(r.length()-1);
        r.append(sep);

        r.append("             COURSE REPORTS\n").append(sep);
        if (coursesMap.isEmpty()) r.append("-\n");
        else coursesMap.values().forEach(c -> r.append(c.generateFullReport()).append(sep));
        
        r.append("            STUDENT REPORTS\n").append(sep);
        if (studentsMap.isEmpty()) r.append("-\n");
        else studentsMap.values().forEach(s -> r.append(s.generateFullReport(GRADE_POINTS)).append(sep));
        
        // Çıktının sonundaki fazladan ayırıcıyı kontrol et
        if (r.length() > 0 && r.lastIndexOf(sep) == r.length() - sep.length() && (coursesMap.isEmpty() || studentsMap.isEmpty()) ) {
            // Eğer son bölüm boşsa ve sadece başlık+sep+sep kaldıysa, bir sep silinebilir.
            // Ama örnek çıktı her zaman en sonda bir sep bırakıyor gibi.
        } else if (r.toString().endsWith(sep+sep)){
             r.delete(r.length() - sep.length(), r.length());
        }


        return r.toString();
    }
    
    // Öğrenci yönetim sistemi için ana yürütme mantığı.
    public void run(String[] args) {
        if (args.length < 7) { /* Kullanım mesajı yazdır ve çık */ return; }
        loadPersons(args[0]);
        loadDepartments(args[1]);
        resolveDepartmentHeads(); // Bölüm başkanlarını ata
        loadPrograms(args[2]);
        loadCourses(args[3]);
        loadAssignments(args[4]);
        loadGrades(args[5]);

        try (PrintWriter out = new PrintWriter(new FileWriter(args[6]))) {
            out.print(consoleAndErrorOutput.toString());
            out.print(generateReports());
        } catch (IOException e) { /* Çıktı yazma hatası */ }
    }
}

// Uygulamanın giriş noktası olarak hizmet veren ana sınıf.
public class Main {
    public static void main(String[] args) {
        new StudentManagementSystem().run(args);
    }
}