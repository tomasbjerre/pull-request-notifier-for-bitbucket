module.exports = function(grunt) {
 grunt.initConfig({
  pkg: grunt.file.readJSON('package.json'),

  jshint: {
   all: [
    'src/main/resources/pr-triggerbutton.js',
    'src/main/resources/admin.js',
    'src/main/resources/utils.js'
   ],
   options: {
    esversion: 3
   }
  },

  jsbeautifier: {
   files: ["Gruntfile.js",
    "src/**/*.vm",
    "src/**/*.xml",
    "pom.xml",
    "src/**/*.js",
    "Gruntfile.js",
    "src/**/*.css"
   ],
   options: {
    html: {
     fileTypes: [".vm", ".xml"],
     indentChar: " ",
     indentSize: 1,
     preserveNewlines: true,
     unformatted: ["a", "sub", "sup", "b", "i", "u", "strong"]
    },
    js: {
     indentChar: " ",
     indentSize: 1
    }
   }
  }
 });

 grunt.loadNpmTasks("grunt-jsbeautifier");
 grunt.loadNpmTasks('grunt-contrib-jshint');

 grunt.registerTask('default', ['jsbeautifier', 'jshint']);
};
