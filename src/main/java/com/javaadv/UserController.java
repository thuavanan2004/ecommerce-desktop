package com.javaadv;

import com.javaadv.Model.User;
import javafx.animation.FadeTransition;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import javafx.util.Duration;

import java.io.File;
import java.io.IOException;
import java.util.Arrays;
import java.util.List;
import java.util.Random;
import java.util.Locale;

public class UserController {
    @FXML private TextField tfAvatarPath;
    @FXML private Button btnOrderManagement;
    @FXML private Button btnCategoryManagement;
    @FXML private TableView<User> userTable;
    @FXML private TableColumn<User, String> colUsername;
    @FXML private TableColumn<User, String> colFullName;
    @FXML private TableColumn<User, String> colAvatar;
    @FXML private TableColumn<User, String> colPassword;
    @FXML private BorderPane mainContent;
    @FXML private Button btnUserManagement;
    @FXML private TextField tfUsername;
    @FXML private PasswordField pfPassword;
    @FXML private TextField tfPasswordVisible;
    @FXML private CheckBox chkShowPassword;
    @FXML private HBox passwordContainer;
    @FXML private TextField tfFullName;
    @FXML private ImageView imgAvatar;
    @FXML private Button btnBrowse;
    @FXML private Button btnAdd;
    @FXML private Button btnUpdate;
    @FXML private Button btnDelete;
    @FXML private Button btnClear;
    @FXML private TextField tfSearch;
    @FXML private Button btnProductManagement;
    @FXML private Button btnRandomUsername;

    private ObservableList<User> userList = FXCollections.observableArrayList();
    private String selectedAvatarPath = "";
    private final List<String> realNames = Arrays.asList(
            "Nguyễn Văn An", "Trần Thị Bình", "Lê Hoàng Cường", "Phạm Thị Dung",
            "Hoàng Văn Đạt", "Vũ Thị E", "Đặng Văn Phúc", "Bùi Thị Giang",
            "Ngô Văn Hải", "Dương Thị Lan", "Mai Văn Minh", "Lý Thị Nga",
            "Trương Văn Khoa", "Nguyễn Thị Hương", "Phan Văn Tài", "Đỗ Thị Mai",
            "Võ Văn Sơn", "Lê Thị Thu", "Hồ Văn Đức", "Trịnh Thị Hồng"
    );

    private final List<String> commonUsernamePrefixes = Arrays.asList(
            "trung", "minh", "hung", "hoang", "thanh", "duc", "anh", "tuan",
            "linh", "huong", "thao", "ngoc", "hai", "phuong", "dung", "long",
            "van", "nam", "thi", "quang", "khanh", "viet", "son", "hoa"
    );

    private final List<String> commonUsernameSuffixes = Arrays.asList(
            "2023", "2024", "2025", "88", "89", "90", "91", "92", "93", "94", "95",
            "vn", "123", "xyz", "official", "real", "pro", "gamer", "dev",
            "2000", "1988", "1990", "1992", "1995", "2001", "999", "hcm", "hn"
    );

    @FXML
    private void initialize() {
        colUsername.setCellValueFactory(new PropertyValueFactory<>("username"));
        colFullName.setCellValueFactory(new PropertyValueFactory<>("fullName"));
        colAvatar.setCellValueFactory(new PropertyValueFactory<>("avatarPath"));

        // Thêm cột hiển thị mật khẩu
        colPassword = new TableColumn<>("Mật khẩu");
        colPassword.setCellValueFactory(new PropertyValueFactory<>("password"));
        userTable.getColumns().add(colPassword);

        // Tạo và cấu hình UI cho hiển thị/ẩn mật khẩu
        setupPasswordToggle();

        for (int i = 0; i < realNames.size(); i++) {
            String username = "user" + (i + 1);
            String fullName = realNames.get(i);
            String password = "pass" + (i + 1);
            String avatar = "https://i.pravatar.cc/150?img=" + (i % 70 + 1); // Sử dụng modulo để đảm bảo không vượt quá 70
            String accessToken = "token" + System.currentTimeMillis() + i;

            userList.add(new User(accessToken, avatar, fullName, password, username));
        }
        userTable.setItems(userList);

        userTable.getSelectionModel().selectedItemProperty().addListener(
                (observable, oldValue, newValue) -> showUserDetails(newValue));

        userTable.setRowFactory(tv -> {
            TableRow<User> row = new TableRow<>();
            row.setOnMouseClicked(event -> {
                if (event.getClickCount() == 2 && (!row.isEmpty())) {
                    User user = row.getItem();
                    showUserInfoDialog(user);
                }
            });
            return row;
        });

        tfSearch.textProperty().addListener((obs, oldText, newText) -> handleSearch());
    }

    private void setupPasswordToggle() {
        // Đảm bảo các thành phần UI đã được khởi tạo
        if (pfPassword == null) {
            pfPassword = new PasswordField();
            pfPassword.setPromptText("Nhập mật khẩu");
        }

        if (tfPasswordVisible == null) {
            tfPasswordVisible = new TextField();
            tfPasswordVisible.setPromptText("Nhập mật khẩu");
            tfPasswordVisible.setVisible(false);
            tfPasswordVisible.setManaged(false);
        }

        if (chkShowPassword == null) {
            chkShowPassword = new CheckBox("Hiển thị mật khẩu");
        }

        // Đồng bộ giá trị giữa PasswordField và TextField
        pfPassword.textProperty().addListener((observable, oldValue, newValue) -> {
            tfPasswordVisible.setText(newValue);
        });

        tfPasswordVisible.textProperty().addListener((observable, oldValue, newValue) -> {
            pfPassword.setText(newValue);
        });

        // Xử lý sự kiện khi checkbox được chọn/bỏ chọn
        chkShowPassword.selectedProperty().addListener((observable, oldValue, newValue) -> {
            pfPassword.setVisible(!newValue);
            pfPassword.setManaged(!newValue);
            tfPasswordVisible.setVisible(newValue);
            tfPasswordVisible.setManaged(newValue);
        });
    }

    private void showUserDetails(User user) {
        if (user != null) {
            tfUsername.setText(user.getUsername());
            String userPassword = user.getPassword();
            pfPassword.setText(userPassword);
            tfPasswordVisible.setText(userPassword);
            tfFullName.setText(user.getFullName());
            selectedAvatarPath = user.getAvatarPath();
            try {
                Image image = new Image(selectedAvatarPath, true);
                imgAvatar.setImage(image);
            } catch (Exception e) {
                imgAvatar.setImage(null);
            }
        }
    }

    private void showUserInfoDialog(User user) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Chi tiết người dùng");
        alert.setHeaderText("Thông tin đầy đủ:");
        alert.setContentText(
                "Tên đăng nhập: " + user.getUsername() + "\n" +
                        "Họ và tên: " + user.getFullName() + "\n" +
                        "Mật khẩu: " + user.getPassword() + "\n" +
                        "Ảnh đại diện: " + user.getAvatarPath() + "\n" +
                        "AccessToken: " + user.getAccessToken()
        );
        alert.showAndWait();
    }

    @FXML
    private void handleAddUser() {
        if (!validateInput()) return;

        User user = new User(
                "token" + System.currentTimeMillis(),
                selectedAvatarPath,
                tfFullName.getText(),
                chkShowPassword.isSelected() ? tfPasswordVisible.getText() : pfPassword.getText(),
                tfUsername.getText()
        );
        userList.add(user);
        userTable.setItems(userList);
        clearFields();
    }

    @FXML
    private void handleUpdateUser() {
        User user = userTable.getSelectionModel().getSelectedItem();
        if (user != null && validateInput()) {
            user.setUsername(tfUsername.getText());
            user.setFullName(tfFullName.getText());
            user.setPassword(chkShowPassword.isSelected() ? tfPasswordVisible.getText() : pfPassword.getText());
            user.setAvatarPath(selectedAvatarPath);
            userTable.refresh();
            clearFields();
        } else {
            showAlert("Không có lựa chọn", "Chọn một người dùng để sửa", "");
        }
    }

    @FXML
    private void handleDeleteUser() {
        User selected = userTable.getSelectionModel().getSelectedItem();
        if (selected != null) {
            // Hiển thị hộp thoại xác nhận trước khi xóa
            Alert confirmDialog = new Alert(Alert.AlertType.CONFIRMATION);
            confirmDialog.setTitle("Xác nhận xóa");
            confirmDialog.setHeaderText("Bạn có chắc chắn muốn xóa người dùng này?");
            confirmDialog.setContentText("Người dùng: " + selected.getFullName() + " (" + selected.getUsername() + ")");

            ButtonType buttonTypeYes = new ButtonType("Có", ButtonBar.ButtonData.YES);
            ButtonType buttonTypeNo = new ButtonType("Không", ButtonBar.ButtonData.NO);
            confirmDialog.getButtonTypes().setAll(buttonTypeYes, buttonTypeNo);

            confirmDialog.showAndWait().ifPresent(response -> {
                if (response == buttonTypeYes) {
                    userList.remove(selected);
                    clearFields();
                }
            });
        } else {
            showAlert("Không có lựa chọn", "Chọn một người dùng để xóa", "");
        }
    }

    @FXML
    private void handleSearch() {
        String keyword = tfSearch.getText().toLowerCase();
        if (keyword.isEmpty()) {
            userTable.setItems(userList);
        } else {
            ObservableList<User> filteredList = FXCollections.observableArrayList();
            for (User user : userList) {
                if (user.getUsername().toLowerCase().contains(keyword) ||
                        user.getFullName().toLowerCase().contains(keyword)) {
                    filteredList.add(user);
                }
            }
            userTable.setItems(filteredList);
        }
    }

    @FXML
    private void handleClearFields() {
        clearFields();
    }

    @FXML
    private void handleRandomUsername() {
        String randomUsername = generateRealisticUsername();
        tfUsername.setText(randomUsername);
    }

    private String generateRealisticUsername() {
        Random random = new Random();

        // Chọn ngẫu nhiên một phương pháp để tạo username
        int method = random.nextInt(4);

        String username;
        switch (method) {
            case 0: // prefix + số
                username = commonUsernamePrefixes.get(random.nextInt(commonUsernamePrefixes.size())) +
                        random.nextInt(1000);
                break;
            case 1: // prefix + suffix
                username = commonUsernamePrefixes.get(random.nextInt(commonUsernamePrefixes.size())) +
                        commonUsernameSuffixes.get(random.nextInt(commonUsernameSuffixes.size()));
                break;
            case 2: // tên viết tắt + số
                if (!tfFullName.getText().isEmpty()) {
                    username = generateUsernameFromFullName(tfFullName.getText()) +
                            (random.nextInt(2) == 0 ? "" : random.nextInt(1000));
                } else {
                    String randomName = realNames.get(random.nextInt(realNames.size()));
                    username = generateUsernameFromFullName(randomName) +
                            (random.nextInt(2) == 0 ? "" : random.nextInt(1000));
                }
                break;
            default: // prefix + prefix + số
                username = commonUsernamePrefixes.get(random.nextInt(commonUsernamePrefixes.size())) +
                        commonUsernamePrefixes.get(random.nextInt(commonUsernamePrefixes.size())) +
                        random.nextInt(100);
        }

        // Đôi khi thêm một dấu gạch dưới
        if (random.nextInt(10) < 3) {
            int pos = random.nextInt(username.length());
            username = username.substring(0, pos) + "_" + username.substring(pos);
        }

        return username.toLowerCase(Locale.ROOT);
    }

    private String generateUsernameFromFullName(String fullName) {
        String[] parts = fullName.split("\\s+");
        StringBuilder username = new StringBuilder();

        if (parts.length > 0) {
            // Lấy chữ cái đầu của các phần trừ phần cuối cùng
            for (int i = 0; i < parts.length - 1; i++) {
                if (!parts[i].isEmpty()) {
                    username.append(parts[i].charAt(0));
                }
            }

            // Thêm phần cuối cùng đầy đủ
            if (parts.length > 0 && !parts[parts.length - 1].isEmpty()) {
                username.append(parts[parts.length - 1]);
            }
        }

        return username.toString().toLowerCase(Locale.ROOT);
    }

    private boolean validateInput() {
        if (tfUsername.getText().isEmpty() ||
                (pfPassword.isVisible() && pfPassword.getText().isEmpty()) ||
                (tfPasswordVisible.isVisible() && tfPasswordVisible.getText().isEmpty()) ||
                tfFullName.getText().isEmpty()) {
            showAlert("Lỗi nhập liệu", "Vui lòng nhập đầy đủ thông tin", "");
            return false;
        }
        return true;
    }

    @FXML
    private void handleStatistics() {
        int totalUsers = userList.size();
        int withAvatar = 0;
        int weakPasswords = 0;

        for (User user : userList) {
            if (user.getAvatarPath() != null && !user.getAvatarPath().trim().isEmpty()) {
                withAvatar++;
            }
            if (user.getPassword().length() < 6) {
                weakPasswords++;
            }
        }

        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Thống kê người dùng");
        alert.setHeaderText("Kết quả thống kê:");
        alert.setContentText(
                "Tổng số người dùng: " + totalUsers + "\n" +
                        "Số người có ảnh đại diện: " + withAvatar + "\n" +
                        "Số người có mật khẩu yếu (< 6 ký tự): " + weakPasswords
        );
        alert.showAndWait();
    }

    private void clearFields() {
        tfUsername.clear();
        pfPassword.clear();
        tfPasswordVisible.clear();
        tfFullName.clear();
        tfAvatarPath.clear();
        selectedAvatarPath = "";
        userTable.getSelectionModel().clearSelection();
    }

    private void showAlert(String title, String header, String content) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle(title);
        alert.setHeaderText(header);
        alert.setContentText(content);
        alert.showAndWait();
    }

    @FXML
    private void handleBrowseAvatar() {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Select Avatar Image");
        fileChooser.getExtensionFilters().addAll(
                new FileChooser.ExtensionFilter("Image Files", "*.png", "*.jpg", "*.jpeg")
        );

        File selectedFile = fileChooser.showOpenDialog(null);
        if (selectedFile != null) {
            selectedAvatarPath = selectedFile.getAbsolutePath();
            Image image = new Image(selectedFile.toURI().toString());
            imgAvatar.setImage(image);
        }
    }

    @FXML
    private void handleLogout(ActionEvent event) throws IOException {
        Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
        FadeTransition fadeOut = new FadeTransition(Duration.seconds(0.5), stage.getScene().getRoot());
        fadeOut.setFromValue(1.0);
        fadeOut.setToValue(0.0);
        fadeOut.setOnFinished(e -> {
            try {
                FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/fxml/Login.fxml"));
                Parent root = loader.load();
                Scene scene = new Scene(root, 825, 400);
                stage.setTitle("ADMIN SYSTEM");
                stage.setScene(scene);
                stage.centerOnScreen();
                FadeTransition fadeIn = new FadeTransition(Duration.seconds(0.25), scene.getRoot());
                fadeIn.setFromValue(0.0);
                fadeIn.setToValue(1.0);
                fadeIn.play();
            } catch (IOException ex) {
                ex.printStackTrace();
            }
        });
        fadeOut.play();
    }

    @FXML
    public void handleOverview(ActionEvent event) {
        Stage stage = (Stage) btnUserManagement.getScene().getWindow();
        SceneManager.changeScene(stage, "/com/fxml/Dashboard.fxml", "Quản lý người dùng");
    }

    @FXML
    public void handleProductManagement(ActionEvent event) {
        Stage stage = (Stage) btnUserManagement.getScene().getWindow();
        SceneManager.changeScene(stage, "/com/fxml/ProductManagement.fxml", "Quản lý người dùng");
    }

    @FXML
    public void handleCategoryManagement(ActionEvent event) {
        Stage stage = (Stage) btnCategoryManagement.getScene().getWindow();
        SceneManager.changeScene(stage, "/com/fxml/CategoryManagement.fxml", "Quản lý người dùng");
    }

    @FXML
    public void handleOrderManagement(ActionEvent event) {
        Stage stage = (Stage) btnOrderManagement.getScene().getWindow();
        SceneManager.changeScene(stage, "/com/fxml/OrderManagement.fxml", "Quản lý người dùng");
    }
}