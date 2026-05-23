import 'package:flutter/material.dart';
import 'package:habit_tracker/screens/main_scaffold.dart';
import '../services/api_service.dart';
import 'home_screen.dart';
import 'package:shared_preferences/shared_preferences.dart'; 
 // WE NEED THIS FOR NAVIGATION!

class LoginScreen extends StatefulWidget {
  const LoginScreen({super.key});

  @override
  _LoginScreenState createState() => _LoginScreenState();
}

class _LoginScreenState extends State<LoginScreen> {
  final TextEditingController _usernameController = TextEditingController();
  final TextEditingController _passwordController = TextEditingController();
  
  bool _isLoading = false;
  bool _isLoginMode = true; // 👈 1. The Magic Toggle Switch
  bool _rememberMe = false;

  // 2. Combine Login and Register into one smart function
  Future<void> _authenticate() async {
    setState(() { _isLoading = true; });

    final username = _usernameController.text.trim();
    final password = _passwordController.text.trim();

    Map<String, dynamic>? userData;

    // 🔀 The Crossroads: Which API do we call?
    if (_isLoginMode) {
      userData = await ApiService.login(username, password);
    } else {
      userData = await ApiService.register(username, password);
    }

    setState(() { _isLoading = false; });

    // 3. The Result Check
    if (userData != null) {
      // Success! Grab the ID and go to the Main App
      int loggedInUserId = userData['user_id'];
      
      if (_rememberMe) {
        final prefs = await SharedPreferences.getInstance();
        await prefs.setInt('userId', loggedInUserId);
        await prefs.setString('username', username);
      }

      if (!mounted) return;
      Navigator.pushReplacement(
        context,
        MaterialPageRoute(
          builder: (context) => MainScaffold(userId: loggedInUserId, username: username), // Pass the username too!
        ),
      );
    } else {
      // Fail: Show a popup error message
      if (!mounted) return;
      ScaffoldMessenger.of(context).showSnackBar(
        SnackBar(content: Text(_isLoginMode ? 'Login Failed' : 'Username already taken')),
      );
    }
  }

  @override
  Widget build(BuildContext context) {
    return Scaffold(
      backgroundColor: const Color.fromARGB(255, 30, 29, 29),
      body: Center(
        child: Padding(
          padding: const EdgeInsets.all(24.0),
          child: Column(
            mainAxisAlignment: MainAxisAlignment.center,
            children: [
              // 🔄 Dynamic Title
              Text(
                _isLoginMode ? 'Welcome Back' : 'Create Account',
                style: const TextStyle(fontSize: 28, fontWeight: FontWeight.bold, color: Colors.white),
              ),
              const SizedBox(height: 30),
              
              TextField(
                controller: _usernameController,
                decoration: const InputDecoration(labelText: 'Username', labelStyle: TextStyle(color: Colors.white)),
                style: const TextStyle(color: Colors.white),
              ),
              const SizedBox(height: 16),
              
              TextField(
                controller: _passwordController,
                decoration: const InputDecoration(labelText: 'Password', labelStyle: TextStyle(color: Colors.white)),
                obscureText: true,
                style: const TextStyle(color: Colors.white),
              ),
              const SizedBox(height: 30),

              // 🔄 Dynamic Main Button
              _isLoading
                  ? const CircularProgressIndicator()
                  : ElevatedButton(
                      onPressed: _authenticate, // Calls our new combined function
                      child: Text(_isLoginMode ? 'Login' : 'Sign Up'),
                      style: ElevatedButton.styleFrom(backgroundColor: Colors.white),
                    ),
              
              const SizedBox(height: 16),
              
              Row(
                mainAxisAlignment: MainAxisAlignment.center,
                children: [
                  Checkbox(
                    value: _rememberMe,
                    onChanged: (bool? value) {
                      setState(() {
                        _rememberMe = value ?? false;
                      });
                    },
                  ),
                  
                  const Text('Remember Me', style: TextStyle(color: Colors.white),),
                  
                ],
              ),
              
              const SizedBox(height: 16),
              // 🔄 The Text Button that flips the switch!
              TextButton(
                onPressed: () {
                  setState(() {
                    _isLoginMode = !_isLoginMode; // Flips true to false, or false to true
                  });
                },
                child: Text(
                  _isLoginMode 
                      ? "Don't have an account? Sign Up" 
                      : "Already have an account? Login",
                      style: TextStyle(color: Colors.white),
                ),
              ),
            ],
          ),
        ),
      ),
    );
  }
}