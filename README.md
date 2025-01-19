# 💰 Expense Manager

## 📝 Description
A modern desktop application for managing personal expenses with AI-powered insights and analytics. Built with Java and featuring an intuitive user interface, this application helps users track, analyze, and optimize their spending habits.

## ✨ Key Features
- 🔐 Secure user authentication system
- 💳 Easy expense tracking and management
- 📊 Visual expense analytics with charts
- 🗂️ Smart expense categorization
- 📅 Monthly and total expense tracking
- 🌍 Multi-language support (English, Spanish, French, German)
- 🎨 Light/Dark theme support
- 🤖 AI-powered financial insights using Deepseek AI

## 🛠️ Technical Requirements
- ☕ Java 11 or higher
- 📦 MySQL Database
- 💻 Operating System: Windows, Linux, or macOS

## 🚀 Quick Start

### 1️⃣ Installation
```bash
# Clone the repository
git clone [repository-url]

# Navigate to project directory
cd ExpenseManager

# Run MySQL setup script
./setup_mysql.bat

# Run the application
./run.bat
```

### 2️⃣ Configuration
1. 🔑 Set up your Deepseek AI API key:
   - Get your API key from [platform.deepseek.ai](https://platform.deepseek.ai)
   - Enter it in the application when prompted
   - Or add it to `config.properties`:
     ```properties
     deepseek.api.key=your-api-key-here
     ```

2. 🗄️ Database Configuration:
   - Update `config.properties` with your MySQL credentials:
     ```properties
     db.url=jdbc:mysql://localhost:3306/expense_manager
     db.user=your-username
     db.password=your-password
     ```

## 💡 Usage Guide

### 👤 Getting Started
1. Launch the application
2. Register a new account or log in
3. Start tracking your expenses!

### 📱 Main Features
- 📝 **Add Expenses**: Click the "+" button to add new expenses
- 📊 **View Analytics**: See spending patterns in the Charts tab
- 🔍 **Search**: Filter expenses by date, category, or amount
- 💬 **AI Assistant**: Get insights and answers about your spending
- 🌙 **Theme Toggle**: Switch between light and dark themes
- 🌐 **Language**: Change application language in settings

## 🤝 Contributing
Contributions are welcome! Please feel free to submit a Pull Request.

## 📄 License
This project is licensed under the MIT License - see the LICENSE file for details.

## 🙏 Acknowledgments
- 🎨 UI Design: FlatLaf Look and Feel
- 📊 Charts: JFreeChart
- 🤖 AI Integration: Deepseek AI API
- 📅 Calendar: JCalendar

## 📞 Support
If you encounter any issues or have questions:
1. 📩 Open an issue in the repository
2. 📧 Contact the development team
3. 📚 Check the documentation

## 🔄 Version History
- v1.0.0 - Initial Release
  - 🎯 Basic expense tracking
  - 📊 Analytics dashboard
  - 🤖 AI integration
  - 🌍 Multi-language support